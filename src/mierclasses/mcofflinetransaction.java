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
public class mcofflinetransaction
{
    //classe para segurar informacoes de uma transacao realizada
    public String idstr; //id da transacao
    public String tipostr; //tipo de transacao: deposito, saque, trade
    public String preco_tradestr; //preco da moeda base em moeda cotacao (caso trade)
    public String quantidadestr; //quantidade de moeda (base/cotacao depositada/sacada) (base comprada/vendida)
    public String timestampstr; //timestamp da transacao em string (long)
    
    public double preco_traded;
    public double quantidaded;
    public java.util.Date timestampdate;
    
    public mcofflinetransaction(String id, String tip, String p_trade, String quan, String tm)
    {
        idstr = id;
        tipostr = tip;
        preco_tradestr = p_trade;
        quantidadestr = quan;
        timestampstr = tm;
        
        if (preco_tradestr.equals("(NN)") == false)
            preco_traded = Double.valueOf(preco_tradestr);
        
        quantidaded = Double.valueOf(quantidadestr);
        timestampdate = new java.util.Date(Long.valueOf(timestampstr));
    }
}
