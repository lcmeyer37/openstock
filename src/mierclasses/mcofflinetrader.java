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
    
    public double feecompra; //taxa utilizada para compra
    public double feevenda; //taxa utilizada para venda
    
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
        feecompra = 0.001;
        feevenda = 0.001;
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
    
    // <editor-fold defaultstate="collapsed" desc="Operacoes de Transacao">
    
    //funcao para adicionar fundos de moeda base
    public String realizardeposito_base(double valordeposito)
    {
        //funcao para adicionar fundos de moeda base
        if (valordeposito > 0)
        {
            quantidademoedabase = quantidademoedabase + valordeposito;
            mierclasses.mcofflinetransaction novatransacao = new
                mierclasses.mcofflinetransaction
                (
                    "transaction"+java.util.UUID.randomUUID().toString(),"depositobase", "(NN)", String.valueOf(valordeposito), String.valueOf(System.currentTimeMillis())
                );
            
            transacoes.add(novatransacao);
            return "ok";
        }
        else
        {
            return "erro - depositomenorzero";
        }
    }
    
    //funcao para adicionar fundos de moeda cotacao
    public String realizardeposito_cotacao(double valordeposito)
    {
        if (valordeposito > 0)
        {
            quantidademoedacotacao = quantidademoedacotacao + valordeposito;
            mierclasses.mcofflinetransaction novatransacao = new
                mierclasses.mcofflinetransaction
                (
                    "transaction"+java.util.UUID.randomUUID().toString(),"depositocotacao", "(NN)", String.valueOf(valordeposito), String.valueOf(System.currentTimeMillis())
                );
            
            transacoes.add(novatransacao);
            return "ok";
        }
        else
        {
            return "erro - depositomenorzero";
        }
    }
    
    //funcao para remover fundos de moeda base
    public String realizarsaque_base(double valorsaque)
    {
        if (valorsaque > 0)
        {
            if (valorsaque <= quantidademoedabase)
            {
                quantidademoedabase = quantidademoedabase - valorsaque;
                
                mierclasses.mcofflinetransaction novatransacao = new
                mierclasses.mcofflinetransaction
                (
                    "transaction"+java.util.UUID.randomUUID().toString(),"saquebase", "(NN)", String.valueOf(valorsaque), String.valueOf(System.currentTimeMillis())
                );
                transacoes.add(novatransacao);
                return "ok";
            }
            else
            {
                return "erro - saquemaiorfundos";
            }
        }
        else
        {
            return "erro - saquemenorzero";
        }
    }
    
    //funcao para remover fundos de moeda cotacao
    public String realizarsaque_cotacao(double valorsaque)
    {
        if (valorsaque > 0)
        {
            if (valorsaque <= quantidademoedacotacao)
            {
                quantidademoedacotacao = quantidademoedacotacao - valorsaque;
                
                mierclasses.mcofflinetransaction novatransacao = new
                mierclasses.mcofflinetransaction
                (
                    "transaction"+java.util.UUID.randomUUID().toString(),"saquecotacao", "(NN)", String.valueOf(valorsaque), String.valueOf(System.currentTimeMillis())
                );
                transacoes.add(novatransacao);
                return "ok";
            }
            else
            {
                return "erro - saquemaiorfundos";
            }

        }
        else
        {
            return "erro - saquemenorzero";
        }
    }
    
    //funcao para realizar compra de moeda base, utilizando moeda cotacao
    public String realizarcompra_basecotacao(double quantidadecomprabase)
    {
        if (quantidadecomprabase > 0)
        {
            double custocompra = custototalcompra_basecotacao(quantidadecomprabase);
            
            if (custocompra <= quantidademoedacotacao)
            {
                quantidademoedacotacao = quantidademoedacotacao - custocompra;
                quantidademoedabase = quantidademoedabase + quantidadecomprabase;
                
                mierclasses.mcofflinetransaction novatransacao = new
                mierclasses.mcofflinetransaction
                (
                    "transaction"+java.util.UUID.randomUUID().toString(),"compra", String.valueOf(melhorask), String.valueOf(quantidadecomprabase), String.valueOf(System.currentTimeMillis())
                );
                transacoes.add(novatransacao);
                return "ok";
            }
            else
            {
                return "erro - moedacotacaoinsuficiente"; 
            } 
        }
        else
        {
            return "erro - quantidadecompramenorzero";
        }
    }
    
    public String realizarvenda_basecotacao(double quantidadevendabase)
    {
        if (quantidadevendabase > 0)
        {
            double ganhovenda = ganhototalvenda_basecotacao(quantidadevendabase);
            
            if (quantidadevendabase <= quantidademoedabase)
            {
                quantidademoedabase = quantidademoedabase - quantidademoedabase;
                quantidademoedacotacao = quantidademoedacotacao + ganhovenda;

                mierclasses.mcofflinetransaction novatransacao = new
                mierclasses.mcofflinetransaction
                (
                    "transaction"+java.util.UUID.randomUUID().toString(),"venda", String.valueOf(melhorbid), String.valueOf(quantidadevendabase), String.valueOf(System.currentTimeMillis())
                );
                transacoes.add(novatransacao);
                return "ok";
            }
            else
            {
                return "erro - moedabaseinsuficiente"; 
            } 
        }
        else
        {
            return "erro - quantidadevendamenorzero";
        }
    }
    
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes de Ajuda">
    public double custototalcompra_basecotacao(double quantidade)
    {
        //funcao para retornar o custo total de compra de moeda base com moeda cotacao
        double custosemtaxa = quantidade*melhorask;
        double taxacompra = custosemtaxa*feecompra;
        double custototal = custosemtaxa + taxacompra;
        
        return custototal;
    }
    
    public double ganhototalvenda_basecotacao(double quantidade)
    {
        //funcao para retornar o ganho total de venda de moeda base para moeda cotacao
        double ganhosemtaxa = quantidade*melhorbid;
        double taxavenda = ganhosemtaxa*feevenda;
        double ganhototal = ganhosemtaxa - taxavenda;
        
        return ganhototal;
    }
    // </editor-fold>
}
