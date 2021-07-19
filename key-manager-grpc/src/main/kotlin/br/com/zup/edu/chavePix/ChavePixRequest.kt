package br.com.zup.edu.chavePix

import br.com.zup.edu.donoDaConta.DonoDaConta
import br.com.zup.edu.servicosExternos.DadosDaContaResponse
import br.com.zup.edu.validacoes.Chave
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
@Chave
data class ChavePixRequest(
    @field:NotBlank val idCliente: String,
    @field:NotBlank val tipoChave: TipoChaveData,
    @field:NotBlank @field:Size(max=77) val valorChave: String,
    @field:NotBlank val tipoConta: TipoContaData
) {

    fun paraChavePix(dadosDaContaResponse: DadosDaContaResponse): ChavePix {
        val donoDaConta = DonoDaConta(dadosDaContaResponse)

        return ChavePix(idCliente,
            tipoChave,
            valorChave = if(this.tipoChave == TipoChaveData.CHAVE_ALEATORIA) UUID.randomUUID().toString() else this.valorChave,
            tipoConta,
            donoDaConta)
    }
}