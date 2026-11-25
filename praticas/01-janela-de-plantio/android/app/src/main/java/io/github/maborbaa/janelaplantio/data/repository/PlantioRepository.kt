package io.github.maborbaa.janelaplantio.data.repository

import io.github.maborbaa.janelaplantio.data.model.CulturaDto
import io.github.maborbaa.janelaplantio.data.model.MunicipioDto
import io.github.maborbaa.janelaplantio.data.source.AgroApiDataSource
import io.github.maborbaa.janelaplantio.data.source.PlantioDataSource

data class AvaliacaoSoloCiclo(
    val solo: String,
    val ciclo: String,
    val riscoPercentual: Int?,
    val portaria: String?
)

data class ResultadoConsultaZarc(
    val cultura: String,
    val municipio: String,
    val uf: String,
    val dataFormatada: String,
    val decendioNumero: Int,
    val avaliacoes: List<AvaliacaoSoloCiclo>
)

class PlantioRepository(
    private val dataSource: PlantioDataSource = AgroApiDataSource()
) {
    private var cacheCulturas: List<CulturaDto>? = null
    private var cacheMunicipios: List<MunicipioDto>? = null

    suspend fun carregarCulturas(): List<CulturaDto> {
        return cacheCulturas ?: dataSource.obterCulturas().also { cacheCulturas = it }
    }

    suspend fun carregarMunicipios(): List<MunicipioDto> {
        return cacheMunicipios ?: dataSource.obterMunicipios().also { cacheMunicipios = it }
    }

    suspend fun consultarJanela(
        cultura: CulturaDto,
        municipio: MunicipioDto,
        dia: Int,
        mes: Int
    ): ResultadoConsultaZarc {
        val indiceDecendio = calcularIndiceDecendio(dia, mes)
        val registros = dataSource.obterZoneamento(municipio.codigoIBGE, cultura.id)

        val avaliacoes = registros.map { item ->
            AvaliacaoSoloCiclo(
                solo = item.solo,
                ciclo = item.ciclo,
                riscoPercentual = item.risco.getOrNull(indiceDecendio),
                portaria = item.portaria
            )
        }

        return ResultadoConsultaZarc(
            cultura = cultura.nome,
            municipio = municipio.nome,
            uf = municipio.uf,
            dataFormatada = "%02d/%02d".format(dia, mes),
            decendioNumero = indiceDecendio + 1,
            avaliacoes = avaliacoes
        )
    }

    companion object {
        fun calcularIndiceDecendio(dia: Int, mes: Int): Int {
            val mesValido = mes.coerceIn(1, 12)
            val diaValido = dia.coerceIn(1, 31)
            val decendioNoMes = ((diaValido - 1) / 10).coerceAtMost(2)
            return (mesValido - 1) * 3 + decendioNoMes
        }
    }
}