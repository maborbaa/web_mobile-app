package io.github.maborbaa.janelaplantio.data.source

import android.util.Base64
import io.github.maborbaa.janelaplantio.BuildConfig
import io.github.maborbaa.janelaplantio.data.model.CulturaDto
import io.github.maborbaa.janelaplantio.data.model.MunicipioDto
import io.github.maborbaa.janelaplantio.data.model.ZoneamentoDto
import io.github.maborbaa.janelaplantio.data.remote.AgroApiClient

interface PlantioDataSource {
    suspend fun obterCulturas(): List<CulturaDto>
    suspend fun obterMunicipios(): List<MunicipioDto>
    suspend fun obterZoneamento(codigoIBGE: Int, idCultura: Int): List<ZoneamentoDto>
}

class AgroApiDataSource : PlantioDataSource {
    private var cachedToken: String? = null
    private var tokenExpiresAtMs: Long = 0L

    private suspend fun getBearerHeader(): String {
        val now = System.currentTimeMillis()
        val current = cachedToken
        if (current != null && now < tokenExpiresAtMs) {
            return "Bearer $current"
        }

        val key = BuildConfig.AGROAPI_KEY
        val secret = BuildConfig.AGROAPI_SECRET
        require(key.isNotBlank() && secret.isNotBlank()) {
            "Chaves AGROAPI_KEY e AGROAPI_SECRET não configuradas no local.properties."
        }

        val credentials = "$key:$secret"
        val encoded = Base64.encodeToString(credentials.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        val response = AgroApiClient.authApi.gerarToken("Basic $encoded")

        cachedToken = response.accessToken
        tokenExpiresAtMs = now + (response.expiresIn - 60) * 1000L
        return "Bearer ${response.accessToken}"
    }

    override suspend fun obterCulturas(): List<CulturaDto> {
        val bearer = getBearerHeader()
        return AgroApiClient.agritecApi.listarCulturas(bearer)
            .data
            .filter { it.hasZoneamento }
            .sortedBy { it.nome }
    }

    override suspend fun obterMunicipios(): List<MunicipioDto> {
        val bearer = getBearerHeader()
        return AgroApiClient.agritecApi.listarMunicipios(bearer)
            .data
            .sortedBy { it.nome }
    }

    override suspend fun obterZoneamento(codigoIBGE: Int, idCultura: Int): List<ZoneamentoDto> {
        val bearer = getBearerHeader()
        return AgroApiClient.agritecApi.consultarZoneamento(bearer, codigoIBGE, idCultura).data
    }
}