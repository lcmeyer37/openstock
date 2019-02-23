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
public class mccandle
{
    //classe que contem informacoes de um candle
    public String timestampstr;
    public String openstr;
    public String highstr;
    public String closestr;
    public String lowstr;
    public String volumestr;
    
    public double opend;
    public double highd;
    public double closed;
    public double lowd;
    public double volumed;
    public java.util.Date timestampdate;
    
    //construtor com timestamp em string
    public mccandle(String tsstr, String ostr, String hstr, String cstr, String lstr, String vstr)
    {
        timestampstr = tsstr;
        openstr = ostr;
        highstr = hstr;
        closestr = cstr;
        lowstr = lstr;
        volumestr = vstr;
        
        opend = Double.valueOf(openstr);
        highd = Double.valueOf(highstr);
        closed = Double.valueOf(closestr);
        lowd = Double.valueOf(lowstr);
        volumed = Double.valueOf(volumestr);
        timestampdate = interpretartimestamp(timestampstr);
        

    }
    
    //eh esperado um timestamp no formato YYYY-MM-DD-HH-mm-ss
    java.util.Date interpretartimestamp(String timestampstr)
    {
        java.util.Date timestampretornar;

        Integer ano = 1900;
        Integer mes = 1;
        Integer dia = 0;
        Integer hora = 0;
        Integer minuto = 0;
        Integer segundo = 0;
        
        try
        {
            ano = Integer.parseInt((timestampstr).split("-")[0]);
        }
        catch (java.lang.ArrayIndexOutOfBoundsException excecao)
        {
            ano = 1900;
        }
        
        try
        {
            mes = Integer.parseInt((timestampstr).split("-")[1]);
        }
        catch (java.lang.ArrayIndexOutOfBoundsException excecao)
        {
            mes = 1;
        }
        
        try
        {
             dia = Integer.parseInt((timestampstr).split("-")[2]);
        }
        catch (java.lang.ArrayIndexOutOfBoundsException excecao)
        {
            dia = 0;
        }
        
        try
        {
            hora =  Integer.parseInt((timestampstr).split("-")[3]);
            //mierfuncoeshelper.mostrarmensagem(hora.toString());
        }
        catch (java.lang.ArrayIndexOutOfBoundsException excecao)
        {
            hora = 0;
        }
        
        try
        {
            minuto =  Integer.parseInt((timestampstr).split("-")[4]);
            //mierfuncoeshelper.mostrarmensagem(minuto.toString());
        }
        catch (java.lang.ArrayIndexOutOfBoundsException excecao)
        {
            minuto = 0;
        }
        
        try
        {
            segundo =  Integer.parseInt((timestampstr).split("-")[5]);
            //mierfuncoeshelper.mostrarmensagem(segundo.toString());
        }
        catch (java.lang.ArrayIndexOutOfBoundsException excecao)
        {
            segundo = 0;
        }

        timestampretornar = new java.util.Date(ano-1900,mes-1,dia,hora,minuto,segundo);
        
        return timestampretornar;
    }
}
