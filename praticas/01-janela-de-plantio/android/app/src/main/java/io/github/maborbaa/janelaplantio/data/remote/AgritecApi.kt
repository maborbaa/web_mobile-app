package io.github.maborbaa.janelaplantio.data.remote

import io.github.maborbaa.janelaplantio.data.model.AgritecResponse
import io.github.maborbaa.janelaplantio.data.model.CulturaDto
import io.github.maborbaa.janelaplantio.data.model.MunicipioDto
import io.github.maborbaa.janelaplantio.data.model.TokenResponse
import io.github.maborbaa.janelaplantio.data.model.ZoneamentoDto
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface EmbrapaAuthApi {
    @FormUrlEncoded
    @POST("token")
    suspend fun gerarToken(
        @Header("Authorization") basicAuth: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): TokenResponse
}

interface AgritecApi {
    @GET("culturas")
    suspend fun listarCulturas(
        @Header("Authorization") bearerToken: String
    ): AgritecResponse<CulturaDto>

    @GET("municipios")
    suspend fun listarMunicipios(
        @Header("Authorization") bearerToken: String
    ): AgritecResponse<MunicipioDto>

    @GET("zoneamento/municipios/{codigoIBGE}/culturas/{idCultura}")
    suspend fun consultarZoneamento(
        @Header("Authorization") bearerToken: String,
        @Path("codigoIBGE") codigoIBGE: Int,
        @Path("idCultura") idCultura: Int
    ): AgritecResponse<ZoneamentoDto>
}