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
    
    //parametro utilizado como variavel externa ao script
    //o usuario ao criar um script utiliza esta variavel para manter valor "entre runs"
    //esta variavel eh uma string, e o usuario pode utiliza-la da forma que achar mais interessante
    public Object externalvariable_lastrun;
    
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

            //<editor-fold defaultstate="collapsed" desc="Associar inputs do Engine para o Script">
            
            //candles
            candles_lastrun = candlesscript;
            engine.put("candles", candles_lastrun);
            
            //versao bearcode
            String bcversion = "1.0a";
            engine.put("bearcodeversion", bcversion);
            
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
            
            //pontos x para plotar o indicador
            try
            {
                pontosx_lastrun = engine.get("xvalues");  
            }
            catch (Exception e)
            {
                //ignorar caso null etc
            }

            //pontos y para plotar o indicador
            try
            {
                pontosy_lastrun = engine.get("yvalues");
            }
            catch (Exception e)
            {
                //ignorar caso null etc
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
            
            //descricao do script de indicador (contem nome do script, e informacao de como plotar a curva do indicador)
            try
            {
                descricaoscript_lastrun = engine.get("scriptdescription");
            }
            catch (Exception e)
            {
                //ignorar caso null etc
            }

            tituloscript_lastrun = ((String)descricaoscript_lastrun).split(";")[0];
            tipoplot_lastrun = ((String)descricaoscript_lastrun).split(";")[1];
            
            //</editor-fold>
        }
        catch (Exception ex)
        {
            return ex.getMessage();
        }
        
        return "ok";
    }

}
