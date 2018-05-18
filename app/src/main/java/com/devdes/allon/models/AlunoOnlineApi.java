package com.devdes.allon.models;


import com.eclipsesource.json.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AlunoOnlineApi {

    // Urls gerais
    private final String URL_API_GERAL = "http://aluno-online.devdes.com.br";
    private final String URL_API_USUARIO_RSA = URL_API_GERAL + "/usuariorsa";
    private final String URL_API_USUARIO_RSA_AUTH = URL_API_USUARIO_RSA + "/auth";

    // Urls Especificas
    private final String URL_API_LOGIN = URL_API_USUARIO_RSA + "/login";
    private final String URL_API_DADOS_CADASTRAIS = URL_API_USUARIO_RSA_AUTH + "/dados";

    // Client
    OkHttpClient client;

    public AlunoOnlineApi() {
        this.client = new OkHttpClient();
    }

    private String requestPost(String url, String json) {

        ResponseBody resBody;
        MediaType JSON;
        Request.Builder builder;
        RequestBody body;
        Request request;
        Response response;

        JSON = MediaType.parse("application/json; charset=utf-8");

        body = RequestBody.create(JSON, json);

        builder = new Request.Builder();

        builder.url(url);
        builder.post(body);

        request = builder.build();

        try {

            response = this.client.newCall(request).execute();

            resBody = response.body();

            return resBody != null ? resBody.string() : null;

        }catch (Exception e){
            return null;
        }

    }

    public ObjetosApi.RespostaLogin login(Integer matricula, String senha) {

        String respJson;
        JsonObject loginReq;
        JsonObject loginRes;


        loginReq = new JsonObject();

        loginReq.add("matricula", matricula);
        loginReq.add("senha", senha);

        respJson = requestPost(URL_API_LOGIN, loginReq.toString());

        if(respJson == null) {
            return null;
        }

        loginRes = JsonObject.readFrom(respJson);

        if(loginRes.get("codigo").asInt() != 200){
            return null;
        }

        return new ObjetosApi.RespostaLogin(
                loginRes.get("dado").asObject().get("token").asString(),
                loginRes.get("dado").asObject().get("identificador").asInt()
        );

    }

    public ObjetosApi.RespostaMeusDados dadosCadastrais(ObjetosApi.RespostaLogin respostaLogin) {
        String respJson;

        JsonObject dadosReq;
        JsonObject dadosRes;
        JsonObject dadosResObjDados;

        ObjetosApi.RespostaMeusDados meusDados;


        dadosReq = new JsonObject();
        meusDados = new ObjetosApi.RespostaMeusDados();

        dadosReq.add("token", respostaLogin.getToken());
        dadosReq.add("identificador", respostaLogin.getIdentificador());

        respJson = requestPost(URL_API_DADOS_CADASTRAIS, dadosReq.toString());

        if(respJson == null) {
            return null;
        }

        dadosRes = JsonObject.readFrom(respJson);

        if(dadosRes.get("codigo").asInt() != 200){
            return null;
        }

        dadosResObjDados = dadosRes.get("dado").asObject();

        JsonObject cabecalho = dadosResObjDados.get("cabecalho").asObject();

        meusDados.urlFoto = cabecalho.get("url_foto").asString();
        meusDados.nome = cabecalho.get("nome").asString();
        meusDados.curso = cabecalho.get("curso").asString();
        meusDados.turma = cabecalho.get("turma").asString();
        meusDados.matricula = cabecalho.get("matricula").asString();

        JsonObject dadosCadastrais = dadosResObjDados.get("dados_cadastrais").asObject();

        JsonObject pessoal = dadosCadastrais.get("pessoal").asObject();

        meusDados.nomeSocial = pessoal.get("nome_social").asString();
        meusDados.dataNascimento = pessoal.get("data_nascimento").asString();
        meusDados.estadoCivil = pessoal.get("estado_civil").asString();
        meusDados.nomePai = pessoal.get("nome_pai").asString();
        meusDados.nomeMae = pessoal.get("nome_mae").asString();
        meusDados.corRaca = pessoal.get("cor_raca").asString();
        meusDados.profissao = pessoal.get("profissao").asString();
        meusDados.localTrabalho = pessoal.get("local_trabalho").asString();
        meusDados.naturalidade = pessoal.get("naturalidade").asString();
        meusDados.nacionalidade = pessoal.get("nacionalidade").asString();

        JsonObject endereco = dadosCadastrais.get("endereco").asObject();

        meusDados.endereco = endereco.get("endereco").asString();
        meusDados.numero = endereco.get("numero").asString();
        meusDados.bairro = endereco.get("bairro").asString();
        meusDados.complemento = endereco.get("complemento").asString();
        meusDados.cep = endereco.get("cep").asString();
        meusDados.cidade = endereco.get("cidade").asString();
        meusDados.estado = endereco.get("estado").asString();

        JsonObject contato = dadosCadastrais.get("contato").asObject();

        meusDados.telTrabalho = contato.get("tel_trabalho").asString();
        meusDados.telResidencia = contato.get("tel_residencia").asString();
        meusDados.telCelular = contato.get("tel_celular").asString();
        meusDados.telResponsavel = contato.get("tel_responsavel").asString();
        meusDados.email = contato.get("email").asString();

        JsonObject documento = dadosCadastrais.get("documento").asObject();

        meusDados.RG = documento.get("rg").asString();
        meusDados.CPF = documento.get("cpf").asString();
        meusDados.orgExp = documento.get("org_exp").asString();


        return meusDados;


    }


}
