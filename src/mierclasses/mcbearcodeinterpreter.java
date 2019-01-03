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
    
    mierpanels.mpitemindicador mpiindicadorpai; //item indicador que contem este objeto bearcode interpreter
    
    //parametros de get utilizados pelo script em sua ultima run
    public java.util.List<mierclasses.mccandle> candles_lastrun;
    
    //parametros de put
    public Object pontosx_lastrun; //deve conter o retorno de pontos para o eixo x
    public Object pontosy_lastrun; //deve conter o retorno de pontos para o eixo y
    public Object outro_lastrun; //pode conter alguma outra informacao que pode ser retornada para o codigo (a ver utilidade)
    public Object descricaoscript_lastrun; 
    //esta variavel diz se o script esta retornando
    //um indicador e o que ele utiliza como eixo y, como timestamp, ou outro valor double
    //e se espera que este grafico seja desenhado acima do grafico original
    //exemplos: "titlescript;typeofyaxis;wheredraw;typeofdraw"
    //para close: "tituloscript;timestamp;drawoncandles;line"
    //para RSI: "tituloscript;timestamp;drawseparate;line
    //para Volume: "tituloscript;timestamp;drawseparate;bar"
    //para Testes: "test"
    public String tituloscript_lastrun;
    public String localdesenho_lastrun;
    public String tipoeixoy_lastrun;
    public String tipodesenho_lastrun;
    
    //classe utilizada para intepretacao de codigo bear code
    public mcbearcodeinterpreter(String id, String nome, String codbcjs, String paramsbcjs, mierpanels.mpitemindicador mpiipai)
    {
        idbcode = id;
        nomebcode = nome;
        codigobcodejs = codbcjs;
        parametrosbcodejs = paramsbcjs;
        mpiindicadorpai = mpiipai;

    }
    
    public String rodarscript()
    {
        try{
            
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
        candles_lastrun = mpiindicadorpai.submodulografico.mcg.candlesatual;
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
        //adicionar uma variavel out especial, para debug de scripts
        engine.put("debugoutput",System.out);

        //rodar script
        //mierclasses.mcfuncoeshelper.mostrarmensagem("codigo para rodar: " + codigobcodejs)
        engine.eval(codigobcodejs);

        //receber valores de x (pode ter ateh 5 valores em x para um y) e y calculados pelo script
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

        ////print para teste
        //mierclasses.mcfuncoeshelper.mostrarmensagem("pontosx1: " + pontosx_lastrun.toString());
        //mierclasses.mcfuncoeshelper.mostrarmensagem("pontosy: " + pontosy_lastrun.toString());
        ////mierclasses.mcfuncoeshelper.mostrarmensagem("outro: " + outro_lastrun.toString());
        //mierclasses.mcfuncoeshelper.mostrarmensagem("scriptdescription: " + descricaoscript_lastrun.toString());

        tituloscript_lastrun = ((String)descricaoscript_lastrun).split(";")[0];
        localdesenho_lastrun = ((String)descricaoscript_lastrun).split(";")[2];
        //diz aonde desenhar os resultados x e y, em cima do grafico ohlc, ou desenhar separadamente (a implementar)

        tipoeixoy_lastrun = ((String)descricaoscript_lastrun).split(";")[1];
        //diz qual o tipo de dado no eixo y, timestamp ou genericodouble
        tipodesenho_lastrun = ((String)descricaoscript_lastrun).split(";")[3];
        //diz qual o tipo de desenho do grafico: line, bars, etc
        }
        catch (Exception ex)
        {
            return ex.getMessage();
        }
        
        return "ok";
    }

}
