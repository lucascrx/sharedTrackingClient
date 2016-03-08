package com.example.sharedtracking.inputs;

import java.sql.Timestamp;

import com.example.sharedtracking.views.ConstantGUI;

public class DialogInputSanitizer {
	
	
	/**Sanitizes user provided tokens as public and private IDs
	 *  Condition : .{25,50} 
	 * @param input
	 * @throws DialogInputException
	 */
	public static void sanitizeInputAsPublicID(String input) throws DialogInputException{
		if(input==null || !input.matches( ".{25,50}" )){
			throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_INVALID_PUBLIC_ID);
		}
	}
	
	/**Sanitizes user provided names as session and device name 
	 * Condition : .{4,50}
	 * @param input
	 * @throws DialogInputException
	 */
	public static void sanitizeInputAsName(String input) throws DialogInputException{
		if(input==null || !input.matches( ".{4,50}" )){
			throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_INVALID_NAME);
		}
		
	}
	
	/**Sanitizes user provided Upload Rate
	 *Condition : not null greater than 0
	 * @param input
	 * @throws DialogInputException
	 */
	public static void sanitizeInputAsRate(Integer input) throws DialogInputException{
		if(input == null || input<=0 ){
			throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_INVALID_RATE);
		}
	}
	
	/**Sanitizes user provided password
	 *Condition : .{10,50}
	 * @param input
	 * @throws DialogInputException
	 */
	public static void sanitizeInputAsPassword(String input) throws DialogInputException{
		if(input==null || !input.matches( ".{10,50}" )){
			throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_INVALID_PASSWORD);
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
		if(input==null ){
			throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_START_NOT_SET);
		}if(now.after(input)){
			throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_START_OCCURED);
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
		if(input==null ){
			throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_START_NOT_SET);
		}if(now.after(input)){
			throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_START_OCCURED);
		}if(input.after(endTime)){
			throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_START_AFTER_END);
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
		if(input==null ){
			throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_END_NOT_SET);
		}if(now.after(input)){
			throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_END_OCCURED);
		}if(input.before(startTime)){
			throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_END_BEFORE_START);
		}
	}
	
	/**When starting time is changed once session has been created, modification requires an extra checking:
	 * *Starting time can only be postponed, this modification must occur before session current starting time
	 * @throws DialogInputException */
	public static void verifyStartingTimeSynchro(Timestamp newStartTime, Timestamp oldStartTime) throws DialogInputException{
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		if(newStartTime==null ){
			throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_START_NOT_SET);
		}if(now.after(oldStartTime)){
			throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_OLD_START_OCCURED);
		}if(oldStartTime.after(newStartTime)){
			throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_START_WRONG_SCHEDULING);
		}
	}
	
	/**When ending time is changed once session has been created, modification requires an extra checking:
	 * the new ending time must be before now, this modification must occur before session current ending time
	 * @throws DialogInputException */
	public static void verifyEndingTimeSynchro(Timestamp newEndTime, Timestamp oldEndTime) throws DialogInputException{
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		if(oldEndTime!=null){
			if(newEndTime==null ){
				throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_END_NOT_SET);
			}if(now.after(oldEndTime)){
				throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_OLD_END_OCCURED);
			}if(now.after(newEndTime)){
				throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_END_OCCURED);
			}
		}else{
			if(newEndTime==null ){
				throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_END_NOT_SET);
			}if(now.after(newEndTime)){
				throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_END_OCCURED);
			}
		}
	}
	
}
