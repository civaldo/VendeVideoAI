# VendeVídeo AI — versão inicial Android

Aplicativo gratuito inicial para criar roteiros promocionais no celular.

## O que já funciona no MVP
- Escolher uma foto do produto na galeria.
- Digitar nome, características, benefícios e preço.
- Gerar localmente um roteiro de vendas em português.
- Ouvir o texto com o recurso de texto-para-fala do Android.
- Ajustar a entonação para um estilo mais agudo ou mais grave.
- Copiar o roteiro.

## O que ainda não está implementado
- Converter foto em vídeo MP4 com inteligência artificial.
- Gerar imagens em movimento, cenas, legendas animadas ou montagem automática.
- Selecionar vozes brasileiras independentes garantidas; as vozes disponíveis dependem do Android.
- Publicar automaticamente em Shopee, TikTok, YouTube ou Facebook.

A foto selecionada não é analisada por IA nesta versão. O roteiro usa as informações digitadas pelo usuário. Não são necessárias chaves de API para as funções atuais.

## Compilação do APK
O workflow GitHub Actions está em `.github/workflows/build-apk.yml`. Ele compila um APK de depuração e publica o arquivo como artefato quando a execução termina com sucesso.

1. Abra **Actions** no repositório.
2. Selecione **Build VendeVideoAI APK** e a execução mais recente.
3. Se estiver concluída com sucesso, baixe o artefato `VendeVideoAI-debug-apk`.

O APK de depuração serve para teste e não é assinado para publicação na Play Store.
