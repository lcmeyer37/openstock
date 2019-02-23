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
public class mcwebcomms
{
    //classe utilizada para comunicacao com paginas web
    
    public mcwebcomms()
    {
        //construtor vazio
    }
    
    public String receberconteudopagina(String enderecoUrl)
    {
        java.net.URL url;
        java.io.InputStream is = null;
        java.io.BufferedReader br;
        String line;

        String retornoconteudo = "";
        
        try 
        {
            url = new java.net.URL(enderecoUrl);
            is = url.openStream();  // throws an IOException
            br = new java.io.BufferedReader(new java.io.InputStreamReader(is));

            while ((line = br.readLine()) != null) 
            {
                //System.out.println(line);
                //return line;
                retornoconteudo = retornoconteudo + line;
            }
        } 
        catch (java.net.MalformedURLException mue) 
        {
             //mue.printStackTrace();
            mcfuncoeshelper.mostrarmensagem( "Um erro ocorreu: " + mue.toString());
        } 
        catch (java.io.IOException ioe) 
        {
            mcfuncoeshelper.mostrarmensagem( "Um erro ocorreu: " + ioe.toString());
        } 
        finally 
        {
            try 
            {
                if (is != null) is.close();
            } 
            catch (java.io.IOException ioe) 
            {
                // nothing to see here
            }
        }
        
        return retornoconteudo;
    }
}

