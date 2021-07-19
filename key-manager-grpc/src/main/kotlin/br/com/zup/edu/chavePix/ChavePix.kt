package br.com.zup.edu.chavePix

import br.com.zup.edu.donoDaConta.DonoDaConta
import javax.persistence.*

@Entity
class ChavePix(
    val idCliente: String,
    @Enumerated
    val tipoChave: TipoChaveData,
    val valorChave: String,
    @Enumerated
    val tipoConta: TipoContaData,
    @Embedded
    val donoDaConta: DonoDaConta
) {

    @Id
    @GeneratedValue
    var id: Long? = null
}
