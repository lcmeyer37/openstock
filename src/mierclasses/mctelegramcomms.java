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
 * @author meyerlu
 */
public class mctelegramcomms
{
    //classe utilizada para comunicacao web
    mcwebcomms mwcomms;
    
    //classe criada para associar o Open Stock com um BOT do Telegram
    //Algumas das funções pensadas para este bot são:
    //- Avisar quando comprar, vender, etc segundo o trader bot.
    //- Avisar se a acao chegar a um certo preco.
    //- Etc.
    String bottoken = ""; //contem a token do bot do telegram para controle via Open Stock
    String userid = ""; //contem o user id da conta telegram do usuario
    
    public boolean ativo = false; //variavel que diz se a api do telegram esta ativada ou nao
    
    public mctelegramcomms()
    {
        mwcomms = new mcwebcomms();
    }
    
    public void setarboteusuario(String bt, String uid)
    {
        bottoken = bt;
        userid = uid;
    }
    
    public void enviarmensagemtelegramcombot(String mensagem)
    {
        String conteudopagina = mwcomms.receberconteudopagina("https://api.telegram.org/bot"+bottoken+"/sendMessage?chat_id="+userid+"&text="+mensagem);
    }
    
    public String retornarbottoken()
    {
        return bottoken;
    }
    
    public String retornaruserid()
    {
        return userid;
    }
}
