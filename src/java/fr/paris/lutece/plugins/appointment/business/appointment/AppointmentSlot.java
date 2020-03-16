package fr.paris.lutece.plugins.appointment.business.appointment;

public class AppointmentSlot {

	// Variables declarations 
    private int _nIdAppointment;
    private int _nIdSlot;
    private int _nNbPlaces;


   /**
    * Returns the IdAppointment
    * @return The IdAppointment
    */ 
    public int getIdAppointment()
    {
        return _nIdAppointment;
    }

   /**
    * Sets the IdAppointment
    * @param nIdAppointment The IdAppointment
    */ 
    public void setIdAppointment( int nIdAppointment )
    {
        _nIdAppointment = nIdAppointment;
    }

   /**
    * Returns the IdSlot
    * @return The IdSlot
    */ 
    public int getIdSlot()
    {
        return _nIdSlot;
    }

   /**
    * Sets the IdSlot
    * @param nIdSlot The IdSlot
    */ 
    public void setIdSlot( int nIdSlot )
    {
        _nIdSlot = nIdSlot;
    }

   /**
    * Returns the NbPlaces
    * @return The NbPlaces
    */ 
    public int getNbPlaces()
    {
        return _nNbPlaces;
    }

   /**
    * Sets the NbPlaces
    * @param nNbPlaces The NbPlaces
    */ 
    public void setNbPlaces( int nNbPlaces )
    {
        _nNbPlaces = nNbPlaces;
    }
}
