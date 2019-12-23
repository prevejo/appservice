# Introdução
Este projeto consiste no provedor de serviços da solução Prevejo.


# Configuração
Configurações do projeto são feitas nos arquivos .properties da pasta ./src/main/java/resources. Configurações relacionadas serviços externos:

--> api.lambda.previsao : consiste na URL http referente a API solicita o serviço lambda de previsão de embarque.

--> api.newsapi.key : API key para utilização do serviço NewsAPI.

--> api.places.key : API key para utilização do serviço Google Places API.


# Observações

# Dependências
As dependências do projeto são gerenciadas pela ferramenta Maven.

# ./libs/
A pasta possui dependências que não podem ser obtidas por repositório web.

O instalação das mesmas no repositório local é feita com o comando:

----> mvn clean

# Execução
Execução com base de dados em memória com perfil dev:

----> mvn spring-boot:run -P dev

# Empacotamento
Produção do pacote ./target/App.jar, já com o perfil prod predefinido:

----> mvn package -Dspring.profiles.active=prod

# Desenvolvimento com base de dados em memória
devmem consiste em um perfil de execução configurado com o banco de dados em memória h2.

O arquivo ./src/main/java/resources/schema-h2.sql produz o esquema da base de dados.

O arquivo ./src/main/java/resources/data-h2.sql produz os dados da base de dados.

O arquivo data-h2.sql pode ser obtido extraindo o arquivo ./src/main/java/resources/data-h2.7z.


# Docker
O build do arquivo Dockerfile constrói uma imagem coletando o arquivo previamente produzido ./target/App.jar.
Quando executada em um container, o App.jar é inicializado.