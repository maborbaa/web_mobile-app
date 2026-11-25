# web_mobile-app

Práticas de desenvolvimento **web e mobile**, com o registro do que fiz e do que aprendi em cada uma. Faz parte do meu portfólio de estudos em [github.com/maborbaa](https://github.com/maborbaa), que também reúne repositórios de FPGA, firmware embarcado e visão computacional.

> English summary: hands-on web and mobile practices, each one with a log of what I built and learned. The first practice is an Android (Kotlin) app that consumes Embrapa's AgroAPI.

## Como este repositório funciona

- Cada prática fica em `praticas/NN-nome/`, com README próprio: objetivo, o que fiz, o que aprendi e como rodar.
- As práticas são **isoladas**: dá para ler e executar cada uma sem depender das outras.
- O [DIARIO.md](DIARIO.md) registra, em entradas curtas, o andamento de todas as práticas.
- Para começar uma prática nova, copie o [modelo](praticas/_modelo/README.md).

## Práticas

| # | Prática | Tecnologias | Status | O que aprendi |
| --- | --- | --- | --- | --- |
| 01 | [Janela de Plantio](praticas/01-janela-de-plantio/) | Android, Kotlin, Jetpack Compose, AgroAPI/Embrapa | MVP funcional | Autenticação OAuth2 e Retrofit na Agritec v2, MVVM com StateFlow e cálculo de decêndios ZARC |

## Como acompanhar

- Use **Watch** ou **Star** no repositório para ser avisado de novidades.
- Leia o [DIARIO.md](DIARIO.md) para ver o que foi feito em cada sessão de trabalho.
- Abra o README de cada prática para ver o status, os detalhes técnicos e o roadmap.

## Convenções

- Chaves, tokens e segredos **nunca** são versionados (`.env`, `local.properties`).
- Commits pequenos, com mensagem descritiva.
- O README de cada prática reflete o estado real: o que ainda não existe aparece como "planejado".

## Autor

Marcio Barbosa, Piracicaba/SP. [Perfil no GitHub](https://github.com/maborbaa) | [LinkedIn](https://www.linkedin.com/in/marciobar/)
