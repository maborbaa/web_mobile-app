package io.github.maborbaa.janelaplantio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.maborbaa.janelaplantio.data.model.CulturaDto
import io.github.maborbaa.janelaplantio.data.model.MunicipioDto
import io.github.maborbaa.janelaplantio.data.repository.AvaliacaoSoloCiclo
import io.github.maborbaa.janelaplantio.data.repository.ResultadoConsultaZarc
import io.github.maborbaa.janelaplantio.ui.PlantioViewModel
import io.github.maborbaa.janelaplantio.ui.theme.JanelaDePlantioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JanelaDePlantioTheme {
                JanelaPlantioScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JanelaPlantioScreen(
    viewModel: PlantioViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Janela de Plantio • ZARC") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.carregandoInicial -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Carregando catálogos da Embrapa...")
                    }
                }

                state.culturas.isEmpty() && state.mensagemErro != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.mensagemErro ?: "Erro desconhecido",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(onClick = { viewModel.carregarCatalogos() }) {
                            Text("Tentar novamente")
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SeletorMunicipio(
                            municipios = state.municipios,
                            selecionado = state.municipioSelecionado,
                            onSelecionar = { viewModel.selecionarMunicipio(it) }
                        )

                        SeletorCultura(
                            culturas = state.culturas,
                            selecionada = state.culturaSelecionada,
                            onSelecionar = { viewModel.selecionarCultura(it) }
                        )

                        SeletorDataPlantio(
                            dia = state.dia,
                            mes = state.mes,
                            onDataAlterada = { novoDia, novoMes ->
                                viewModel.atualizarData(novoDia, novoMes)
                            }
                        )

                        Button(
                            onClick = { viewModel.consultar() },
                            enabled = !state.consultando &&
                                    state.culturaSelecionada != null &&
                                    state.municipioSelecionado != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (state.consultando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Consultando ZARC...")
                            } else {
                                Text("Consultar Janela de Plantio")
                            }
                        }

                        state.mensagemErro?.let { erro ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Text(
                                    text = erro,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        state.resultado?.let { resultado ->
                            CardResultadoZarc(resultado = resultado)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeletorMunicipio(
    municipios: List<MunicipioDto>,
    selecionado: MunicipioDto?,
    onSelecionar: (MunicipioDto) -> Unit
) {
    var busca by remember(selecionado) {
        mutableStateOf(selecionado?.let { "${it.nome} - ${it.uf}" } ?: "")
    }
    var expandido by remember { mutableStateOf(false) }

    val filtrados = remember(busca, municipios) {
        val termo = busca.trim().uppercase()
        if (termo.isEmpty()) {
            municipios.take(25)
        } else {
            municipios.filter {
                it.nome.uppercase().contains(termo) ||
                        "${it.nome} - ${it.uf}".uppercase().contains(termo)
            }.take(25)
        }
    }

    Column {
        OutlinedTextField(
            value = busca,
            onValueChange = {
                busca = it
                expandido = true
            },
            label = { Text("Município (digite para filtrar)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (expandido && filtrados.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    filtrados.forEach { municipio ->
                        Text(
                            text = "${municipio.nome} - ${municipio.uf}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    busca = "${municipio.nome} - ${municipio.uf}"
                                    expandido = false
                                    onSelecionar(municipio)
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeletorCultura(
    culturas: List<CulturaDto>,
    selecionada: CulturaDto?,
    onSelecionar: (CulturaDto) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = !expandido }
    ) {
        OutlinedTextField(
            value = selecionada?.nome ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Cultura Agrícola") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            modifier = Modifier.heightIn(max = 280.dp)
        ) {
            culturas.forEach { cultura ->
                DropdownMenuItem(
                    text = { Text(cultura.nome) },
                    onClick = {
                        onSelecionar(cultura)
                        expandido = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeletorDataPlantio(
    dia: Int,
    mes: Int,
    onDataAlterada: (Int, Int) -> Unit
) {
    val meses = listOf(
        1 to "Janeiro", 2 to "Fevereiro", 3 to "Março", 4 to "Abril",
        5 to "Maio", 6 to "Junho", 7 to "Julho", 8 to "Agosto",
        9 to "Setembro", 10 to "Outubro", 11 to "Novembro", 12 to "Dezembro"
    )
    var diaExpandido by remember { mutableStateOf(false) }
    var mesExpandido by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = diaExpandido,
            onExpandedChange = { diaExpandido = !diaExpandido },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = "%02d".format(dia),
                onValueChange = {},
                readOnly = true,
                label = { Text("Dia") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = diaExpandido) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = diaExpandido,
                onDismissRequest = { diaExpandido = false },
                modifier = Modifier.heightIn(max = 240.dp)
            ) {
                (1..31).forEach { d ->
                    DropdownMenuItem(
                        text = { Text("%02d".format(d)) },
                        onClick = {
                            onDataAlterada(d, mes)
                            diaExpandido = false
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = mesExpandido,
            onExpandedChange = { mesExpandido = !mesExpandido },
            modifier = Modifier.weight(2f)
        ) {
            OutlinedTextField(
                value = meses.firstOrNull { it.first == mes }?.second ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Mês pretendido") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mesExpandido) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = mesExpandido,
                onDismissRequest = { mesExpandido = false },
                modifier = Modifier.heightIn(max = 240.dp)
            ) {
                meses.forEach { (numero, nome) ->
                    DropdownMenuItem(
                        text = { Text(nome) },
                        onClick = {
                            onDataAlterada(dia, numero)
                            mesExpandido = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CardResultadoZarc(resultado: ResultadoConsultaZarc) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${resultado.cultura} • ${resultado.municipio}/${resultado.uf}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Data: ${resultado.dataFormatada} (${resultado.decendioNumero}º decêndio do ano)",
                style = MaterialTheme.typography.bodyMedium
            )

            if (resultado.avaliacoes.isEmpty()) {
                Text(
                    text = "Sem zoneamento ZARC cadastrado para esta cultura neste município.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                resultado.avaliacoes.forEach { avaliacao ->
                    ItemAvaliacaoSolo(avaliacao)
                }
            }
        }
    }
}

@Composable
private fun ItemAvaliacaoSolo(avaliacao: AvaliacaoSoloCiclo) {
    val recomendado = avaliacao.riscoPercentual != null
    val corFundo = if (recomendado) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val corTexto = if (recomendado) Color(0xFF1B5E20) else Color(0xFFB71C1C)

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = corFundo,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Solo ${avaliacao.solo} • ${avaliacao.ciclo}",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = if (recomendado) "Risco ${avaliacao.riscoPercentual}%" else "Não indicado",
                    fontWeight = FontWeight.Bold,
                    color = corTexto
                )
            }
            avaliacao.portaria?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}