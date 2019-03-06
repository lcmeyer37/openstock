/*
 * Copyright (C) 2019 Lucas Meyer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    //outra variavel que o usuario pode colocar uma string para ser printada em exports de simulacao
    public Object debugexport_lastrun;

    //parametro utilizado como variavel externa ao script
    //o usuario ao criar um script utiliza esta variavel para manter valor "entre runs"
    //esta variavel eh uma string, e o usuario pode utiliza-la da forma que achar mais interessante
    public Object externalvariable_lastrun;
    
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

            //<editor-fold defaultstate="collapsed" desc="Associar inputs do Engine para o Script">
            
            //candles
            candles_lastrun = candlesscript;
            engine.put("candles", candles_lastrun);
            
            //versao bearcode
            String bcversion = "1.0a";
            engine.put("bearcodeversion", bcversion);
            
            //quantidade moeda base
            quantidadebase_lastrun = qmbasescript;
            engine.put("basefunds", quantidadebase_lastrun);
            
            //quantidade moeda cotacao
            quantidadecotacao_lastrun = qmcotacaoscript;
            engine.put("quotefunds", quantidadecotacao_lastrun);
            
            //ultimo bid
            bid_lastrun = bidscript;
            engine.put("lastbid", bid_lastrun);
            
            //ultimo ask
            ask_lastrun = askscript;
            engine.put("lastask", ask_lastrun);
            
            //fee compra
            feecompra_lastrun = feecomprascript;
            engine.put("buyfee", feecompra_lastrun);
            
            //fee venda
            feevenda_lastrun = feevendascript;
            engine.put("sellfee", feevenda_lastrun);

            //debugoutput (utilizado para output no system.out e debug em dev)
            engine.put("debugoutput",System.out);

            //runoutput (utilizado dentro do bearcode editor para prints de teste)
            if (adicionarouthandler == true)
            {
                engine.put("runoutput", runoutput);
            }
            
            //extvar (utilizado como variavel, para conter valores entre runs)
            engine.put("extvar",externalvariable_lastrun);
            
            //</editor-fold>

            //rodar script
            engine.eval(codigobcodejs);

            //<editor-fold defaultstate="collapsed" desc="Associar outputs do Script para o Engine">
            
            //decisao do trader
            try
            {
                respostatradermove_lastrun = engine.get("tradermove");
            }
            catch (Exception e)
            {
                //
            }
            
            //montante de suporte para a decisao
            try
            {
                respostaquantidadesuporte_lastrun = engine.get("supportamount");  
            }
            catch (Exception e)
            {
                //
            }
            
            //debugexport (utilizado para customizar output em coluna exportada pelo csv)
            try
            {
                debugexport_lastrun = engine.get("debugexport");
            }
            catch (Exception e)
            {
                debugexport_lastrun = "";
            }
            
            //extvar (utilizado como variavel, para conter valores entre runs)
            try
            {
                externalvariable_lastrun = engine.get("extvar");
            }
            catch (Exception e)
            {
                externalvariable_lastrun = "";
            }
            //</editor-fold>
        }
        catch (Exception ex)
        {
            return ex.getMessage();
        }
        
        return "ok";
    }

}
