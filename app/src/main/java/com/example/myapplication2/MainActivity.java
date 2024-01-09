package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // Declaração das variáveis de interface
    private EditText editQuantidadeDolar;
    private TextView textResultado;
    private TextView textDolar;

    // Método executado quando a Activity é criada
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editQuantidadeDolar = findViewById(R.id.editQuantidadeDolar);
        textResultado = findViewById(R.id.textResultado);
        textDolar = findViewById(R.id.textDolar);

        // Chama o método para buscar os dados assim que o aplicativo é aberto
        new ConverteMoedaTask().execute(1.0);
    }

    // Método chamado quando o botão de conversão é clicado
    public void converter(View view) {
        // Obtém a quantidade de moeda inserida pelo usuário
        double quantidadeMoeda = Double.parseDouble(editQuantidadeDolar.getText().toString());
        // Inicia a tarefa assíncrona para converter a moeda
        new ConverteMoedaTask().execute(quantidadeMoeda);
    }
    // Classe interna que estende AsyncTask para realizar a conversão da moeda em segundo plano
    private class ConverteMoedaTask extends AsyncTask<Double, Void, Double[]> {

        private double cotacaoDolar;
        // Método executado em uma thread separada para realizar a requisição à API
        @Override
        protected Double[] doInBackground(Double... params) {
            double quantidadeMoeda = params[0];
            Double[] resultados = new Double[2];

            try {
                // Criação e configuração da conexão com a API
                URL url = new URL("https://economia.awesomeapi.com.br/last/USD-BRL");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                // Leitura da resposta da API
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Processando o JSON retornado
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject usdToBrl = jsonObject.getJSONObject("USDBRL");
                String cotacaoString = usdToBrl.getString("bid");
                cotacaoDolar = Double.parseDouble(cotacaoString);

                resultados[0] = quantidadeMoeda * cotacaoDolar; // Armazena o resultado
                resultados[1] = cotacaoDolar; // Armazena a cotação do dólar
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultados;
        }

        // Método executado após a conclusão da tarefa em segundo plano
        @Override
        protected void onPostExecute(Double[] resultados) {
            // Atualização da interface com os resultados da conversão
            if (resultados != null && resultados.length == 2) {
                textDolar.setText("Valor do Dolar: " + resultados[1]);
                textResultado.setText("O valor convertido em R$: " + resultados[0]);
            } else {
                // Tratativa de erro
                textDolar.setText("Valor do Dolar: N/A");
                textResultado.setText("O valor convertido em R$: N/A");
            }
        }
    }
}