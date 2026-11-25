package io.github.maborbaa.janelaplantio.data.model

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Long
)

data class AgritecResponse<T>(
    val data: List<T> = emptyList()
)

data class CulturaDto(
    val id: Int,
    val nome: String,
    val nomeCompleto: String? = null,
    val cultivo: String? = null,
    val hasZoneamento: Boolean = false
)

data class MunicipioDto(
    val codigoIBGE: Int,
    val nome: String,
    val uf: String
)

data class ZoneamentoDto(
    val cultura: String,
    val solo: String,
    val ciclo: String,
    val risco: List<Int?> = emptyList(),
    val portaria: String? = null
)