package com.example.sharedtracking.inputs;

import java.sql.Timestamp;

public class DialogInputSanitizer {
	
	
	/**Sanitizes user provided tokens as public and private IDs
	 *  Condition : .{25,50} 
	 * @param input
	 * @throws DialogInputException
	 */
	public static void sanitizeInputAsPublicID(String input) throws DialogInputException{
		if(input==null || !input.matches( ".{25,50}" )){
			throw new DialogInputException("'"+input+"' doesn't have a valid ID format");
		}
	}
	
	/**Sanitizes user provided names as session and device name 
	 * Condition : .{4,255}
	 * @param input
	 * @throws DialogInputException
	 */
	public static void sanitizeInputAsName(String input) throws DialogInputException{
		if(input==null || !input.matches( ".{4,255}" )){
			throw new DialogInputException("'"+input+"' doesn't have a valid Name format");
		}
		
	}
	
	/**Sanitizes user provided Upload Rate
	 *Condition : not null greater than 0
	 * @param input
	 * @throws DialogInputException
	 */
	public static void sanitizeInputAsRate(Integer input) throws DialogInputException{
		if(input == null || input<=0 ){
			throw new DialogInputException("The provided rate is null");
		}
	}
	
	/**Sanitizes user provided password
	 *Condition : .{10,255}
	 * @param input
	 * @throws DialogInputException
	 */
	public static void sanitizeInputAsPassword(String input) throws DialogInputException{
		if(input==null || !input.matches( ".{10,255}" )){
			throw new DialogInputException("'"+input+"' doesn't have a valid Password format");
		}
	}
	
	/**Sanitizes user provided starting time in a stand alone way
	 * Condition : after current time
	 * @param input
	 * @throws DialogInputException
	 */
	public static void sanitizeInputAsStartingDateAlone(Timestamp input) throws DialogInputException{
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		if(input==null || now.after(input)){
			throw new DialogInputException("Starting time is null or has already occured");
		}
	}		
	
	/**Sanitizes user provided starting time using end Time
	 * Condition : after current time and before end Time
	 * @param input
	 * @throws DialogInputException
	 */
	public static void sanitizeStartTime(Timestamp input, Timestamp endTime) throws DialogInputException{
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		if(input==null || now.after(input) || input.after(endTime)){
			throw new DialogInputException("Starting time has already occured, or is after end Time");
		}
	}
	
	
	/**Sanitizes user provided ending time using start Time
	 * Condition : after current time and  after start Time
	 * @param input
	 * @throws DialogInputException
	 */
	public static void sanitizeInputAsEndingDate(Timestamp input, Timestamp startTime) throws DialogInputException{
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		if(input==null || now.after(input) || input.before(startTime)){
			throw new DialogInputException("End time has already occured, or is before start Time");
		}
	}
	
	/**When starting time is changed once session has been created, modification requires an extra checking:
	 * *Starting time can only be postponed, this modification must occur before session current starting time
	 * @throws DialogInputException */
	public static void verifyStartingTimeSynchro(Timestamp newStartTime, Timestamp oldStartTime) throws DialogInputException{
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		if(newStartTime==null || now.after(oldStartTime) || oldStartTime.after(newStartTime)){
			throw new DialogInputException("Start Time doesn't fullfil the requirements");
		}
	}
	
	/**When ending time is changed once session has been created, modification requires an extra checking:
	 * the new ending time must be before now, this modification must occur before session current ending time
	 * @throws DialogInputException */
	public static void verifyEndingTimeSynchro(Timestamp newEndTime, Timestamp oldEndTime) throws DialogInputException{
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		if(oldEndTime!=null){
			if(newEndTime==null || now.after(oldEndTime) || now.after(newEndTime)){
				throw new DialogInputException("End Time doesn't fullfil the requirements");
			}
		}else{
			if(newEndTime==null || now.after(newEndTime)){
				throw new DialogInputException("End Time doesn't fullfil the requirements");
			}
		}
	}
	
}
