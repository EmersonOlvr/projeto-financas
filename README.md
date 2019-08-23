# Projeto Finanças
Projeto de finanças pessoais desenvolvido por [Emerson Oliveira](https://github.com/EmersonOlvr), [Laís Souza](https://github.com/Lassouz4) e [Jacilene Lima](https://github.com/jacilima) na matéria "Projeto e Prática 2" do Instituto Federal de Educação, Ciência e Tecnologia de Pernambuco (IFPE) - Campus Jaboatão dos Guararapes durante o último semestre letivo.

## Colocando o projeto para rodar
Pré-requisitos:
- Java instalado. Pode ser baixado em [https://www.java.com/pt_BR/download/](https://www.java.com/pt_BR/download/).
- Maven instalado. Pode ser baixado em [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi).

Primeiro, edite o arquivo application.properties (localizado em [src/main/resources/application.properties](https://github.com/EmersonOlvr/projeto-financas/blob/master/src/main/resources/application.properties)) para configurar o banco de dados, usuário e senha do MySQL. Em seguida, dentro da raiz do projeto, digite `mvn package`. Após isso, digite `java -jar target/projeto-financas-x-x-x-SNAPSHOT.jar`, substítuindo `x-x-x-SNAPSHOT` pela versão atual do projeto (pode ser vista no [pom.xml](https://github.com/EmersonOlvr/projeto-financas/blob/master/pom.xml)).

Depois, basta abrir o navegador e acessar http://localhost:8090
