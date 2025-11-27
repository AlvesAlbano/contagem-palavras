import java.nio.charset.StandardCharsets;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

import static javax.swing.UIManager.getString;
import static org.jocl.CL.*;


public class BuscarPalavras {

    public static void SerialCPU(String[] texto,String palavraChave){

        final long tempoInicio = System.nanoTime();

        final int ocorrencias = contarOcorrencias(texto,palavraChave);

        final long tempoFim = System.nanoTime();

        final long tempoExecucao = tempoFim - tempoInicio;


        // System.out.printf("SerialCPU: %d ocorrências em %d ms. Palavra Chave: %s\n",ocorrencias,tempoExecucao,palavraChave);
    }

    public static void ParallelCPU(String[] texto, String palavraChave,int quantidadeThreads){
        try (ForkJoinPool forkJoinPool = new ForkJoinPool(quantidadeThreads)) {

            int n = texto.length;

            int inicio = 0;
            int fim = n - 1;

            final int FATOR_DIVISAO_PARALELA = 4;
            final int LIMITE_MINIMO_SUBLISTAS = Math.max(n / (quantidadeThreads * FATOR_DIVISAO_PARALELA), 10000);

            final long tempoInicio = System.nanoTime();

            final int ocorrencias = forkJoinPool.invoke(new ContarOcorrenciasTask(texto, palavraChave, inicio, fim, LIMITE_MINIMO_SUBLISTAS));

            final long tempoFim = System.nanoTime();

            final long tempoExecucao = tempoFim - tempoInicio;

            // System.out.printf("ParallelCPU: %d ocorrências em %d ms. Palavra Chave: %s\n",ocorrencias,tempoExecucao,palavraChave);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ParallelGPU(String[] texto, String palavraChave) {

        int[] textoHash = hashTexto(texto);
        int palavraHash = palavraChave.hashCode();
        int n = textoHash.length;

        // Inicializar OpenCL
        CL.setExceptionsEnabled(true);

        final String kernelSource = """
                __kernel void contar(
                    __global const int *texto,
                    const int palavraHash,
                    __global int *resultados)
                {
                    int id = get_global_id(0);
                
                    if (texto[id] == palavraHash)
                        resultados[id] = 1;
                    else
                        resultados[id] = 0;
                }
                
                """;

        // Plataforma e dispositivo
        cl_platform_id[] platforms = new cl_platform_id[1];
        clGetPlatformIDs(1, platforms, null);

        cl_device_id[] devices = new cl_device_id[1];
        clGetDeviceIDs(platforms[0], CL_DEVICE_TYPE_GPU, 1, devices, null);

        cl_context context = clCreateContext(
                null, 1, devices, null, null, null
        );

        cl_command_queue queue = clCreateCommandQueue(
                context, devices[0], 0, null
        );

        cl_program program = clCreateProgramWithSource(
                context, 1, new String[]{ kernelSource }, null, null
        );
        clBuildProgram(program, 0, null, null, null, null);

        final long tempoInicio = System.nanoTime();

        cl_kernel kernel = clCreateKernel(program, "contar", null);

        // Buffers
        cl_mem memTexto = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_int * n, Pointer.to(textoHash), null);

        cl_mem memResultados = clCreateBuffer(context, CL_MEM_WRITE_ONLY,
                Sizeof.cl_int * n, null, null);

        // Argumentos do kernel
        clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(memTexto));
        clSetKernelArg(kernel, 1, Sizeof.cl_int, Pointer.to(new int[]{ palavraHash }));
        clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(memResultados));

        // Execução em paralelo
        long[] globalWorkSize = new long[]{ n };
        clEnqueueNDRangeKernel(queue, kernel, 1, null, globalWorkSize, null, 0, null, null);

        // Ler resultado
        int[] resultados = new int[n];
        clEnqueueReadBuffer(queue, memResultados, CL_TRUE, 0,
                Sizeof.cl_int * n, Pointer.to(resultados), 0, null, null);

        // Somar ocorrências
        int ocorrencias = 0;
        for (int r : resultados) ocorrencias += r;

        // Liberar recursos

        clReleaseMemObject(memTexto);
        clReleaseMemObject(memResultados);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(queue);
        clReleaseContext(context);

        final long tempoFim = System.nanoTime();

        final long tempoExecucao = tempoFim - tempoInicio;

//         System.out.printf("ParallelGPU: %d ocorrências em %d ms. Palavra Chave: %s\n",ocorrencias,tempoExecucao,palavraChave);
    }




    private static int[] hashTexto(String[] texto){

        int[] textoHash = new int[texto.length];

        for (int i = 0; i < textoHash.length; i++) {
            textoHash[i] = texto[i].hashCode();
        }

        return textoHash;
    }

    private static int contarOcorrencias(String[] texto,String palavraChave){

        int ocorrencias = 0;

        for(int i = 0;i < texto.length;i++){
            if (texto[i].equals(palavraChave)){
                ocorrencias++;
            }
        }

        return ocorrencias;
    }

    private static class ContarOcorrenciasTask extends RecursiveTask<Integer> {
        private static String[] conteudo;
        private static String palavraChave;
        private static int inicio;
        private static int fim;
        private static int LIMITE_MINIMO_SUBLISTAS;

        public ContarOcorrenciasTask(String[] conteudo, String palavraChave, int inicio, int fim, int lIMITE_MINIMO_SUBLISTAS) {
            this.conteudo = conteudo;
            this.palavraChave = palavraChave;
            this.inicio = inicio;
            this.fim = fim;
            this.LIMITE_MINIMO_SUBLISTAS = lIMITE_MINIMO_SUBLISTAS;
        }

        private static int contarOcorrencias(String[] conteudo,int inicio,int fim){
            int ocorrencias = 0;

            for (int i = inicio; i <= fim; i++) {
                if (conteudo[i].equals(palavraChave)) {
                    ocorrencias++;
                }
            }

            return ocorrencias;
        }

        @Override
        protected Integer compute() {
            if (fim - inicio + 1 <= LIMITE_MINIMO_SUBLISTAS){
                return contarOcorrencias(conteudo, inicio, fim);
            }

            int meio = (inicio + fim) / 2;

            ContarOcorrenciasTask esquerda = new ContarOcorrenciasTask(conteudo, palavraChave, inicio, meio, LIMITE_MINIMO_SUBLISTAS);
            ContarOcorrenciasTask direita = new ContarOcorrenciasTask(conteudo, palavraChave, meio + 1, fim, LIMITE_MINIMO_SUBLISTAS);

            esquerda.fork();
            int resultadodireita = direita.compute();
            int resultadoesquerda = esquerda.join();

            return resultadoesquerda + resultadodireita;
        }
    }
}

