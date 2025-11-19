import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Arquivo {
    public static String lerArquivo(String caminho) {
        StringBuilder conteudo = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(caminho))) {
            String linha;
            while ((linha = bufferedReader.readLine()) != null) {
                // conteudo.append(linha).append("\n");
                conteudo.append(linha);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conteudo.toString();
    }

    public static void criarArquivoResultado(StringBuilder resultado, String caminho){
        final String pasta = "resultados";

        new File(pasta).mkdirs();

        String nomeArquivo = String.format("%s/%s",pasta,new File(caminho).getName().replace(".txt", "-resultado.txt"));
        
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(nomeArquivo))) {
            bufferedWriter.write(resultado.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.printf("resultado gerado no caminho %s\n",nomeArquivo);
    }
}