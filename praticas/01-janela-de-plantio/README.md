# 01. Janela de Plantio

App Android que consulta **janelas de plantio de baixo risco climático** e **cultivares indicadas**, a partir de dados da plataforma AgroAPI da Embrapa.

> English summary: an Android app (Kotlin + Jetpack Compose) that shows low-risk planting windows and suitable cultivars using Embrapa's AgroAPI. Planned MVP; this README is updated as each stage is completed.

| | |
| --- | --- |
| **Status** | Em planejamento (início em 19/11/2026) |
| **Plataforma** | Android nativo |
| **Linguagem e UI** | Kotlin, Jetpack Compose |
| **Arquitetura** | MVVM (ViewModel + StateFlow), Repository e `DataSource` |
| **Fonte de dados** | AgroAPI/Embrapa (API Agritec). Plano B: Open-Meteo |

## Contexto e problema

Decidir a época de plantio envolve risco climático. A Embrapa publica o Zoneamento Agrícola de Risco Climático (Zarc), e a AgroAPI permite consultá-lo por API. A prática quer mostrar, de forma simples, como transformar esse tipo de dado em uma decisão objetiva na tela do celular.

## Solução (MVP planejado)

1. A pessoa informa **cultura**, **município** e **data de plantio pretendida**.
2. O app consulta a API e indica se a data cai em uma janela de baixo risco.
3. O app lista cultivares indicadas, se o endpoint permitir no escopo do MVP.

Escopo completo, fora de escopo e critérios de aceite em [SPEC.md](SPEC.md).

## Funcionalidades

- [ ] Formulário com cultura, município e data
- [ ] Consulta à API Agritec com autenticação por token
- [ ] Resultado com a janela de plantio e o nível de risco
- [ ] Estados de tela: carregando, sucesso, erro e sem dados
- [ ] Cultivares indicadas
- [ ] Opcional: notas de campo salvas no aparelho (DataStore)

## Arquitetura

```
Tela (Compose)  →  ViewModel (StateFlow)  →  Repository  →  DataSource
                                                              ├─ AgroApiDataSource     (MVP)
                                                              ├─ OpenMeteoDataSource   (plano B)
                                                              └─ SensorDataSource      (futuro)
```

A fonte de dados fica atrás de uma interface. Assim dá para trocar de fornecedor sem mexer na tela e, mais adiante, conectar leituras de sensores embarcados ao mesmo app.

## Dados e acesso à API

- Cadastro e chaves pela [Loja de APIs da AgroAPI](https://www.agroapi.cnptia.embrapa.br/store/): conta, aplicação, Consumer Key e Consumer Secret.
- Com as chaves, a aplicação gera um Access Token. Segundo o guia oficial, a validade padrão é de 60 minutos, então o app precisará renovar o token.
- O guia cita planos gratuitos (por exemplo, `Gratuito1KPorMes`). Os limites vigentes devem ser conferidos na Loja de APIs.
- A API Agritec oferece época de plantio por decêndio com níveis de risco, cultivares indicadas, recomendação de adubação e condições climáticas, conforme a documentação da Embrapa. Os parâmetros exatos dos endpoints serão confirmados na etapa Conhecer.

## Testar o acesso à API (já disponível)

1. Copie `.env.example` para `.env` nesta pasta e preencha `AGROAPI_KEY` e `AGROAPI_SECRET`.
2. Rode, a partir desta pasta:

```bash
python3 scripts/testar_agroapi.py
```

O script pede o token e consulta a lista de culturas da Agritec. Ele não imprime chave, segredo nem token. Os endereços padrão devem ser conferidos no API Console da Loja de APIs; se forem outros, defina `AGROAPI_TOKEN_URL` e `AGROAPI_CULTURAS_URL` no `.env`.

## Segurança

- O `.env` e o `local.properties` estão no `.gitignore` e não vão para o repositório.
- Embutir chave e segredo em um APK permite que alguém os extraia. Para esta prática de estudo isso é aceitável, com as chaves restritas ao plano gratuito. Em um produto real, as chamadas passariam por um servidor intermediário, e esse é um dos passos de evolução previstos.

## Roadmap

Etapas, datas e checklist em [ROADMAP.md](ROADMAP.md). O andamento dia a dia está no [DIARIO.md](../../DIARIO.md) da raiz do repositório.

## O que aprendi

*A preencher conforme as etapas forem concluídas.*

## Aviso

Projeto pessoal e educacional, sem vínculo oficial com a Embrapa. As informações exibidas **não substituem orientação agronômica**.

## Licença

A definir.
