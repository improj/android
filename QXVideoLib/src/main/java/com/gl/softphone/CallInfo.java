package com.gl.softphone;

public class CallInfo  extends Object {
	public String callid;   /* callid */
	public int cmode;		/* call mode */
	public int cstate;		/* call state, eg. single pass */
	public int ctime;		/* offset time between connecting and ringing event */
	public int ismute;		/* mute state*/
	public String mcodec;	/* communicate codec */
	public String mgw;		/* ip of media gateway */
	public int mmode;		/* transmit mode */
	public int mport;		/* port for media gateway */
	public int pktrcv;		/* count for media packets has received */
	public int pktsnd;		/* count for media packets has send */
	public int role;		/* call role, caller or callee */
	public String sgw;		/* ip of signalling gateway */
	public int sprsn;		/* reason for single-pass state */
	
}
