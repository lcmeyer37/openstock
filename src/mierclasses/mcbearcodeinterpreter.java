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
public class mcbearcodeinterpreter
{
    public String idbcode = ""; //id unico dado para este codigo, ex: close
    
    public String nomebcode = ""; //nome dado para este codigo, ex: Close Moving Average
    
    public String codigobcodejs = ""; //codigo em javascript com o algoritmo deste indicador ou regra
    
    public String parametrosbcodejs = ""; 
    //parametros utilizados pelo codigo sao passados neste variavel, e os parametros estao formatados nesta variavel
    //da seguinte forma: nome_param_1=valor_param_1;nome_param_2=valor_param_2;etc...
    //eh possivel tambem que parametros nao sejam necessarios para um certo script, entao esta variavel pode vir
    //com texto vazio ""
    
    //parametros de get utilizados pelo script em sua ultima run
    public java.util.List<mierclasses.mccandle> candles_lastrun;
    
    //parametros de put
    public Object pontosx_lastrun; //deve conter o retorno de pontos para o eixo x
    public Object pontosy_lastrun; //deve conter o retorno de pontos para o eixo y
    public Object outro_lastrun; //pode conter alguma outra informacao que pode ser retornada para o codigo (a ver utilidade)
    public Object descricaoscript_lastrun; //string que explica titulo do indicador e como printar grafico
    public String tituloscript_lastrun; //titulo indicador
    public String tipoplot_lastrun; //tipo de print do plot
    
    //classe utilizada para intepretacao de codigo bear code
    public mcbearcodeinterpreter(String id, String nome, String codbcjs, String paramsbcjs)
    {
        idbcode = id;
        nomebcode = nome;
        codigobcodejs = codbcjs;
        parametrosbcodejs = paramsbcjs;
    }
    
    public void atualizarscriptparametros(String codnovo, String paramsnovo)
    {
        //funcao para reatualizar codigo e parametros (utilizado pelo editor)
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
            //reordenar a lista de candles por timestampdate
            java.util.Collections.sort(candles_lastrun, new java.util.Comparator<mierclasses.mccandle>() 
            {
                public int compare(mierclasses.mccandle candleone, mierclasses.mccandle candletwo) 
                {
                    return candleone.timestampdate.compareTo(candletwo.timestampdate);
                }
            });
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
                outro_lastrun = engine.get("other");
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
