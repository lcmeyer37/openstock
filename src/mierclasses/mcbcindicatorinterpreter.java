/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mierclasses;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author lucasmeyer
 */
public class mcbcindicatorinterpreter
{
    //classe utilizada para interpretar scripts bearcode de indicadores
    
    //id unico dado para este codigo, ex: close
    public String idbcode = ""; 
    //nome dado para este codigo, ex: Close Moving Average
    public String nomebcode = "";
     //codigo em javascript com o algoritmo deste indicador ou regra
    public String codigobcodejs = "";
    //os parametros de input do script do indicador estao formatados nesta variavel
    //da seguinte forma: nome_param_1=valor_param_1;nome_param_2=valor_param_2;etc...
    public String parametrosbcodejs = ""; 
    
    //parametros de envio para o script
    //ultimas candles utilizadas como input ao processar o script
    public java.util.List<mierclasses.mccandle> candles_lastrun;
    
    //parametros de retorno do script
    //deve conter o retorno de pontos para o eixo x
    public Object pontosx_lastrun; 
    //deve conter o retorno de pontos para o eixo y
    public Object pontosy_lastrun;
    //string que explica titulo do indicador e como printar grafico
    public Object descricaoscript_lastrun; 
    //um titulo que o criador do script pode associar para apresentacao na plotagem de grafico
    public String tituloscript_lastrun; 
    //tipo de print do plot
    public String tipoplot_lastrun; 
    
    //construtor
    public mcbcindicatorinterpreter(String id, String nome, String codbcjs, String paramsbcjs)
    {
        idbcode = id;
        nomebcode = nome;
        codigobcodejs = codbcjs;
        parametrosbcodejs = paramsbcjs;
    }
    
    //funcao para reatualizar codigo e parametros (utilizado pelo editor)
    public void atualizarscriptparametros(String codnovo, String paramsnovo)
    {
        codigobcodejs = codnovo;
        parametrosbcodejs = paramsnovo;
    }
    
    public String rodarscript(java.util.List<mierclasses.mccandle> candlesscript, Boolean adicionarouthandler, mierclasses.mcjtextareahandler runoutput)
    {
        try
        {
            
            //funcao para rodar script associado a este bearcodeinterpreter
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");

            //adicionar parametros ao script, inputados pelo usuario
            if (parametrosbcodejs.toLowerCase().contains(";".toLowerCase()))
            {
               String[] paramskeyvalue = parametrosbcodejs.split(";");
               for (int i = 0; i < paramskeyvalue.length; i++)
               {
                    String key = paramskeyvalue[i].split("=")[0];
                    String value = paramskeyvalue[i].split("=")[1];
                    engine.put(key, value);
               } 
            }

            //adicionar candles do submodulo grafico ao script, e informacao de versao atual do bearcode
            candles_lastrun = candlesscript;
            engine.put("candles", candles_lastrun);
            String bcversion = "1.0a";
            engine.put("bearcodeversion", bcversion);

            //debugoutput e utilizado para output no system.out e debug em dev
            engine.put("debugoutput",System.out);

            //run output eh utilizado dentro do bearcode editor para print no jTextAreaOutput
            if (adicionarouthandler == true)
            {
                engine.put("runoutput", runoutput);
            }

            //rodar script
            //mierclasses.mcfuncoeshelper.mostrarmensagem("codigo para rodar: " + codigobcodejs)
            engine.eval(codigobcodejs);

            try
            {
                pontosx_lastrun = engine.get("xvalues");  
            }
            catch (Exception e)
            {
                //ignorar caso null etc
            }

            try
            {
                pontosy_lastrun = engine.get("yvalues");
            }
            catch (Exception e)
            {
                //ignorar caso null etc
            }

            try
            {
                descricaoscript_lastrun = engine.get("scriptdescription");
            }
            catch (Exception e)
            {
                //ignorar caso null etc
            }

            //o usuario pode devolver no momento soh um xarray e yarray para print
            //ele pode escolher se ele quer printar esse dataset no chart ohlc
            //neste caso ele deve escolher a opcao drawoncandles, e essa opcao ira
            //criar um grafico separado e um grafico no ohlc, em linha obrigatoriamente
            //outros casos existentes sao printar somente separado, sem aparecer no ohlc,
            //mas neste caso o grafico pode ser em linha, area ou em barra.
            //(a trabalhar para adicionar multiplas linhas para xarray12345... e yarray
            //tambem soh eh possivel printar para timestamps como eixo y
            
            
            tituloscript_lastrun = ((String)descricaoscript_lastrun).split(";")[0];
            tipoplot_lastrun = ((String)descricaoscript_lastrun).split(";")[1];
        }
        catch (Exception ex)
        {
            return ex.getMessage();
        }
        
        return "ok";
    }

}
