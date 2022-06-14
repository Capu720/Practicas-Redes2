public class Datos {
    public String PuertoRMI;
    public String PuertoEnvio;
    public String ID;
    public String Dirrecion;
    
    public Datos(String PuertoRMI, String PuertoEnvio, String ID, String Dirrecion)
    {
        this.PuertoEnvio = PuertoEnvio;
        this.PuertoRMI = PuertoRMI;
        this.ID = ID;
        this.Dirrecion = Dirrecion;
    }
    
    public String getPuertoRMI()
    {
        return PuertoRMI;
    }
    
    public void setPuertoRMI(String PuertoRMI){
        this.PuertoRMI = PuertoRMI;
    }
    
    public void setPuertoEnvio(String PuertoEnvio){
        this.PuertoEnvio = PuertoEnvio;
    }
    
    public String getID()
    {
        return ID;
    }
    
    public void setID(String ID){
        this.ID = ID;
    }
    
    public String getPuertoEnvio()
    {
        return PuertoEnvio;
    }
    
     public String getDirrecion() {
        return Dirrecion;
    }

    public void setDirrecion(String Dirrecion) {
        this.Dirrecion = Dirrecion;
    }
    

   
    
}
