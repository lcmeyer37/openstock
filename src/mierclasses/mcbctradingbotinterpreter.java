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
public class mcbctradingbotinterpreter
{
    //classe utilizada para interpretar scripts bearcode de trader bots
    /*
    Os scripts bearcode para Trader Bots no momento funcionam da seguinte forma:
    -> Atualizacao de candles
        -> rodar script trader
            -> receber resposta do script como "Manter, Vender, Comprar" como uma quantidade. Por exemplo:
                respostatrader = 300 significa comprar 300 moedas base (comprar)
                respostatrader = 0 significa nao fazer nada (manter)
                repostatrader = -50 significa vender 50 moedas base (vender)
                
                -> atuar em cima da respostatrader e atualizar a lista de transactions do offline trader
    */
    
    
    //id unico dado para este algoritmo de bot, ex: macdtrader
    public String idbcode = ""; 
    //nome dado para este algoritmo de bot, ex: MACD Trader
    public String nomebcode = "";
     //codigo em javascript com o algoritmo deste indicador ou regra
    public String codigobcodejs = "";
    //os parametros de input do script do indicador estao formatados nesta variavel
    //da seguinte forma: nome_param_1=valor_param_1;nome_param_2=valor_param_2;etc...
    public String parametrosbcodejs = ""; 
    
    //parametros de envio para o script
    //candles utilizadas como input ao processar o script
    public java.util.List<mierclasses.mccandle> candles_lastrun;
    //valor de moeda base disponivel utilizada como input ao processar o script
    public double quantidadebase_lastrun;
    //valor moeda cotacao disponivel utilizada como input ao processar o script
    public double quantidadecotacao_lastrun;
    //valor de bid utilizada como input ao processar o script
    public double bid_lastrun;
    //valor de ask como input ao processar o script
    public double ask_lastrun;
    //valor de fee de compra utilizada como input ao processar o script
    public double feecompra_lastrun;
    //valor de fee de venda utilizada como input ao processar o script
    public double feevenda_lastrun;
    
    
    //parametros de retorno do script
    //variavel que diz qual a operacao desejada para o trader
    public Object respostatradermove_lastrun;
    //variavel que recebe um valor de suporte para compra ou venda
    //exemplos: +30 ou -30 pode ser moeda base ou cotacao
    public Object respostaquantidadesuporte_lastrun;

    
    
    //construtor
    public mcbctradingbotinterpreter(String id, String nome, String codbcjs, String paramsbcjs)
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
    
    public String rodarscript
        (
            java.util.List<mierclasses.mccandle> candlesscript,
            double qmbasescript,
            double qmcotacaoscript,
            double bidscript,
            double askscript,
            double feecomprascript,
            double feevendascript,
            Boolean adicionarouthandler, 
            mierclasses.mcjtextareahandler runoutput
        )
    {
        try
        {
            
            //funcao para rodar script associado a este bearcodeinterpreter de trading bot
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
            
            //adicionar quantidades de moeda disponivel ate o momento
            quantidadebase_lastrun = qmbasescript;
            engine.put("basefunds", quantidadebase_lastrun);
            quantidadecotacao_lastrun = qmcotacaoscript;
            engine.put("quotefunds", quantidadecotacao_lastrun);
            
            //adicionar ultimo bid e ask disponivel
            bid_lastrun = bidscript;
            engine.put("lastbid", bid_lastrun);
            ask_lastrun = askscript;
            engine.put("lastask", ask_lastrun);
            
            //adicionar fees de compra e venda
            feecompra_lastrun = feecomprascript;
            engine.put("buyfee", feecompra_lastrun);
            feevenda_lastrun = feevendascript;
            engine.put("sellfee", feevenda_lastrun);

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
                respostatradermove_lastrun = engine.get("tradermove");
            }
            catch (Exception e)
            {
                //
            }
            
            try
            {
                respostaquantidadesuporte_lastrun = engine.get("supportamount");  
            }
            catch (Exception e)
            {
                //
            }
        }
        catch (Exception ex)
        {
            return ex.getMessage();
        }
        
        return "ok";
    }

}
