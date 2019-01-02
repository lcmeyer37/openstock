/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

