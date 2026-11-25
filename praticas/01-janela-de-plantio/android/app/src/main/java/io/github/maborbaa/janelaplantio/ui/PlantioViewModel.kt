package io.github.maborbaa.janelaplantio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maborbaa.janelaplantio.data.model.CulturaDto
import io.github.maborbaa.janelaplantio.data.model.MunicipioDto
import io.github.maborbaa.janelaplantio.data.repository.PlantioRepository
import io.github.maborbaa.janelaplantio.data.repository.ResultadoConsultaZarc
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlantioUiState(
    val carregandoInicial: Boolean = true,
    val consultando: Boolean = false,
    val culturas: List<CulturaDto> = emptyList(),
    val municipios: List<MunicipioDto> = emptyList(),
    val culturaSelecionada: CulturaDto? = null,
    val municipioSelecionado: MunicipioDto? = null,
    val dia: Int = 15,
    val mes: Int = 10,
    val resultado: ResultadoConsultaZarc? = null,
    val mensagemErro: String? = null
)

class PlantioViewModel(
    private val repository: PlantioRepository = PlantioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlantioUiState())
    val uiState: StateFlow<PlantioUiState> = _uiState.asStateFlow()

    init {
        carregarCatalogos()
    }

    fun carregarCatalogos() {
        viewModelScope.launch {
            _uiState.update { it.copy(carregandoInicial = true, mensagemErro = null) }
            try {
                val culturasDeferred = async { repository.carregarCulturas() }
                val municipiosDeferred = async { repository.carregarMunicipios() }

                val culturas = culturasDeferred.await()
                val municipios = municipiosDeferred.await()

                val piracicaba = municipios.find { it.codigoIBGE == 3538709 } ?: municipios.firstOrNull()
                val culturaPadrao = culturas.firstOrNull()

                _uiState.update {
                    it.copy(
                        carregandoInicial = false,
                        culturas = culturas,
                        municipios = municipios,
                        culturaSelecionada = culturaPadrao,
                        municipioSelecionado = piracicaba
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        carregandoInicial = false,
                        mensagemErro = "Falha ao carregar dados da Embrapa: ${e.localizedMessage ?: "verifique a ligação"}"
                    )
                }
            }
        }
    }

    fun selecionarCultura(cultura: CulturaDto) {
        _uiState.update { it.copy(culturaSelecionada = cultura, resultado = null, mensagemErro = null) }
    }

    fun selecionarMunicipio(municipio: MunicipioDto) {
        _uiState.update { it.copy(municipioSelecionado = municipio, resultado = null, mensagemErro = null) }
    }

    fun atualizarData(dia: Int, mes: Int) {
        _uiState.update { it.copy(dia = dia, mes = mes, resultado = null, mensagemErro = null) }
    }

    fun consultar() {
        val estado = _uiState.value
        val cultura = estado.culturaSelecionada ?: return
        val municipio = estado.municipioSelecionado ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(consultando = true, mensagemErro = null, resultado = null) }
            try {
                val res = repository.consultarJanela(cultura, municipio, estado.dia, estado.mes)
                _uiState.update { it.copy(consultando = false, resultado = res) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        consultando = false,
                        mensagemErro = "Erro na consulta ZARC: ${e.localizedMessage ?: "tente novamente"}"
                    )
                }
            }
        }
    }
}