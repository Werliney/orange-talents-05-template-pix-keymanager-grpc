package br.com.zup.edu.donoDaConta

import br.com.zup.edu.servicosExternos.DadosDaContaResponse
import javax.persistence.Embeddable

@Embeddable
class DonoDaConta(dadosDaContaResponse: DadosDaContaResponse) {

    val idDono = dadosDaContaResponse.titular.id
    val nome = dadosDaContaResponse.titular.nome
    val cpf = dadosDaContaResponse.titular.cpf
    val tipo = dadosDaContaResponse.tipo
}