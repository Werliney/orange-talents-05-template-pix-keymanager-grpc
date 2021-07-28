package br.com.zup.edu.exceptions

class ClienteNuloException(mensagem: String = "O cliente não pode ser nulo"): Exception(mensagem) {
}