package br.com.zup.edu.exceptions

class ChaveExistenteException(mensagem: String = "Essa chave já existe"): Exception(mensagem) {
}