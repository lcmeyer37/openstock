/*
 * Copyright (C) 2019 lucasmeyer
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

/**
 *
 * @author lucasmeyer
 */
public class mcofflinetrader
{
    //classe utilizada pelo submoduloofflinetrader para realizar transacoes
    panels.analisadorasset.offlinetrader.submoduloofflinetrader submodulooftraderpai;
    
    
    public String simbolo; //simbolo relacionado a estre trader
    
    public double quantidademoedabase; //quantidade de moeda base que este trader tem (fundos offline)
    public double quantidademoedacotacao; //quantidade de moeda cotacao que este trader tem (fundos offline)
    
    public double melhorbid;
    public double melhorask;
    
    public java.util.List<mierclasses.mcofflinetransaction> transacoes; //lista de transacoes feitar por este trader
    
    //construtor, o offline trader ao ser criado soh tem um simbolo associado a ele, e nenhuma transacao ou dinheiro
    public mcofflinetrader(panels.analisadorasset.offlinetrader.submoduloofflinetrader sopai)
    {
        submodulooftraderpai = sopai;
    }
    
    public void recriarofflinetrader(String simb)
    {
        simbolo = simb;
        quantidademoedabase = 0.0;
        quantidademoedacotacao = 0.0;
        transacoes = new java.util.ArrayList<>();
    }
    
    public void atualizarbidask()
    {
        //funcao para atualizar bid e ask atual deste offline trader
        
        //funcao soh para carregar pela primeira vez os valores do asset atual
        if ((simbolo.equals("testdata")) == true)
        {
            //codigo para criar um dataset offline para teste
            //candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartsample();
            
            java.util.List<Double> bidask = submodulooftraderpai.aassetpai.iaassetpai.tprincipalpai.miex.receberlastbidaskofflinetradingsample();
            double bid = bidask.get(0);
            double ask = bidask.get(1);
            
            melhorbid = bid;
            melhorask = ask;
        }
        else if ((simbolo.equals("testdata")) == false)
        {
            java.util.List<Double> bidask = submodulooftraderpai.aassetpai.iaassetpai.tprincipalpai.miex.receberlastbidaskofflinetrading(simbolo);
            double bid = bidask.get(0);
            double ask = bidask.get(1);
            
            melhorbid = bid;
            melhorask = ask;
        }
    }
}
