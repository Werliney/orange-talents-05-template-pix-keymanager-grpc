package br.com.zup.edu.chavePix

import br.com.zup.edu.donoDaConta.DonoDaConta
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class ChavePix(
    val idCliente: String,
    @Enumerated(EnumType.STRING)
    val tipoChave: TipoChaveData,
    val valorChave: String,
    @Enumerated(EnumType.STRING)
    val tipoConta: TipoContaData,
    @Embedded
    val donoDaConta: DonoDaConta
) {

    @Id
    @GeneratedValue
    var id: Long? = null
}
