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

/**
 *
 * @author lucasmeyer
 */
public class mcofflinetrader
{
    //classe utilizada pelo submoduloofflinetrader ou editor bearcode para realizar transacoes
    analisadorasset.submoduloofflinetrader.panel_submoduloofflinetrader submodulooftraderpai;
    outrasopcoes.editorbearcodetraderbot.frame_editorbearcodetraderbot editorbctraderbotpai;
    String tipopai = ""; //diz se o tipo de pai deste offline trader eh o subm ou o editor
    
    public String simbolo; //simbolo relacionado a este trader
    
    public double quantidademoedabase; //quantidade de moeda base que este trader tem (fundos offline)
    public double quantidademoedacotacao; //quantidade de moeda cotacao que este trader tem (fundos offline)
    
    public double melhorbid;
    public double melhorask;
    
    public double feecompra; //taxa utilizada para compra
    public double feevenda; //taxa utilizada para venda
    
    public java.util.List<mierclasses.mcofflinetransaction> transacoes; //lista de transacoes feitar por este trader
    
    //construtores
    //quando associado a um submoduloofflinetrader
    public mcofflinetrader(analisadorasset.submoduloofflinetrader.panel_submoduloofflinetrader sopai)
    {
        submodulooftraderpai = sopai;
        tipopai = "submoduloofflinetrader";
    }
    //quando associado a um editor
    public mcofflinetrader(outrasopcoes.editorbearcodetraderbot.frame_editorbearcodetraderbot epai)
    {
        editorbctraderbotpai = epai;
        tipopai = "editorbearcodetraderbot";
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
        if (tipopai.equals("submoduloofflinetrader") == true)
        {
            java.util.List<Double> bidask = submodulooftraderpai.submodulohpai.assetpai.iaassetpai.tprincipalpai.msapicomms.offline_simularbidaskcandles(submodulooftraderpai.submodulohpai.subgrafico.mcg.mcg_candles);
            double bid = bidask.get(0);
            double ask = bidask.get(1);
            
            melhorbid = bid;
            melhorask = ask;  
        }
        else if (tipopai.equals("editorbearcodetraderbot") == true)
        {
            
            java.util.List<Double> bidask = editorbctraderbotpai.telappai.msapicomms.offline_simularbidaskcandles(editorbctraderbotpai.candlessimulacao);
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
            valordeposito = corrigirprecisao(valordeposito);
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
            valordeposito = corrigirprecisao(valordeposito);
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
                valorsaque = corrigirprecisao(valorsaque);
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
                valorsaque = corrigirprecisao(valorsaque);
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
            
            //mierclasses.mcfuncoeshelper.mostrarmensagem("custocompra: " + custocompra + "\n" + "quantidademoedacotacao: " + quantidademoedacotacao);
            if (corrigirprecisao(custocompra) <= corrigirprecisao(quantidademoedacotacao))
            {
                custocompra = corrigirprecisao(custocompra);
                quantidadecomprabase = corrigirprecisao(quantidadecomprabase);
                
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
    
    //funcao para realizar venda de moeda base, utilizando moeda cotacao
    public String realizarvenda_basecotacao(double quantidadevendabase)
    {
        if (quantidadevendabase > 0)
        {
            double ganhovenda = ganhototalvenda_basecotacao(quantidadevendabase);
            
            if (corrigirprecisao(quantidadevendabase) <= corrigirprecisao(quantidademoedabase))
            {
                ganhovenda = corrigirprecisao(ganhovenda);
                quantidadevendabase = corrigirprecisao(quantidadevendabase);
                
                quantidademoedabase = quantidademoedabase - quantidadevendabase;
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
    
    //funcao para comprar o maximo possivel de moeda base com moeda cotacao disponivel
    public String realizarcompratudo_basecotacao()
    {
        //na verdade existe um limite de venda total de moeda cotacao
        //ele eh controlado dizendo que o maximo que se pode comprar de moeda base eh o total possivel * 0.99999
        double quantidadebasecomprar = quantidademoedabasedadocotacao(quantidademoedacotacao);
        String resposta = realizarcompra_basecotacao(quantidadebasecomprar);
        return resposta;
    }
    
    //funcao para vender o maximo possivel de moeda base disponivel
    public String realizarvendatudo_basecotacao()
    {
        String resposta = realizarvenda_basecotacao(quantidademoedabase);
        return resposta;
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes de Ajuda">
    public double quantidademoedabasedadocotacao(double quantidadecotacao)
    {
        //funcao para retornar a quantidade de moeda base possivel de comprar
        //dado a quantidade de moeda cotacao de entrada
        //custo total = quantidade cotacao
        //custototal = custosemtaxa + taxacompra
        //custototal = (quantidadebasecomprar*melhorask) + (quantidadebasecomprar*melhorask)*feecompra
        //custototal = quantidadebasecomprar * melhorask + quantidadebasecomprar * melhorask * feecompra
        //custototal = quantidadebasecomprar * (melhorask + 1 * melhorask * feecompra)
        //quantidadebasecomprar = custototal / (melhorask + 1 * melhorask * feecompra)
        
        double custototal = quantidadecotacao;
        double quantidadebasemaxima = custototal/(melhorask + (melhorask*feecompra));
        //mierclasses.mcfuncoeshelper.mostrarmensagem(String.valueOf(quantidadebasemaxima));
        
        return quantidadebasemaxima;
    }
    
    public double custototalcompra_basecotacao(double quantidadebasecomprar)
    {
        //funcao para retornar o custo total de compra de moeda base com moeda cotacao
        atualizarbidask(); //sempre atualizar o bid ask com o ultimo valor antes de venda
        
        double custosemtaxa = quantidadebasecomprar*melhorask;
        double taxacompra = custosemtaxa*feecompra;
        double custototal = custosemtaxa + taxacompra;
        
        return custototal;
    }
    
    public double ganhototalvenda_basecotacao(double quantidadebasevender)
    {
        //funcao para retornar o ganho total de venda de moeda base para moeda cotacao
        atualizarbidask(); //sempre atualizar o bid ask com o ultimo valor antes de venda
        double ganhosemtaxa = quantidadebasevender*melhorbid;
        double taxavenda = ganhosemtaxa*feevenda;
        double ganhototal = ganhosemtaxa - taxavenda;

        return ganhototal;
    }
    
    public double totalfundos_moedacotacao()
    {
        //funcao para retornar o total de fundos deste trader
        return (quantidademoedabase*melhorbid + quantidademoedacotacao);
    }
    
    double corrigirprecisao(double valororiginal)
    {
        return Math.round(valororiginal*1e8)/1e8;
    }
    // </editor-fold>
}
