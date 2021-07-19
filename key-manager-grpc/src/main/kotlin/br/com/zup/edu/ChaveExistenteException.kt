package br.com.zup.edu

class ChaveExistenteException(mensagem: String = "Essa chave já existe"): Exception(mensagem) {
}