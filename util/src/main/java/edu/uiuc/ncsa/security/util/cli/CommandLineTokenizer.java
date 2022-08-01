package edu.uiuc.ncsa.security.util.cli;
import java.io.Serializable;
import java.util.Vector;
/**
 * This is a very specific tokenizer for command lines from the Dumb gridFTP client.
 * It pulls off the elements that are double-quote delimited (to allow for embedded
 * blanks) and then tokenizes by spaces. The first word is assumed to be a command
 * word and is translated to lower case. The others are not.
 * This is intended to be instantiated once and reused. It preserves no state between
 * calls.
 *
 */
public final class CommandLineTokenizer implements Serializable {
	public char getQuoteDelimiter() {
		return quoteDelimiter;
	}

	public void setQuoteDelimiter(char quoteDelimiter) {
		this.quoteDelimiter = quoteDelimiter;
	}

	protected char quoteDelimiter = '"';

	public CommandLineTokenizer(char quoteDelimiter) {
		this.quoteDelimiter = quoteDelimiter;
	}

	public CommandLineTokenizer() {
	}

	public static void main(String[] args) {
		CommandLineTokenizer clt = new CommandLineTokenizer();
		String[] test = { "a [2,5] \"foo\" \"asd  baz\"", "     ", "hash 1024k", "GEt blarf.txt warf.txt", "put \"wo of\" \"blar g\"" };
		Vector v;
		try {
			for (int i = 0; i < test.length; i++) {
				v = clt.tokenize(test[i]);
				System.out.println("elements for argument " + i);
				for (int j = 0; j < v.size(); j++) {
					System.out.println("   " + v.elementAt(j));
				}
				System.out.println("-------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // end main (for debugging)

	/**
	 * tokenize with whatever the current quote delimiter is. Default is double quotes.
	 * @param cl
	 * @return
	 * @throws MalformedCommandException
	 */
	public Vector tokenize(String cl) throws MalformedCommandException {
		return tokenize(cl, getQuoteDelimiter());
	}

	public Vector tokenize(String cl, String quoteDelimiter) throws MalformedCommandException {
		          return tokenize(cl, quoteDelimiter.charAt(0));
	}
	public Vector tokenize(String cl, char quoteDelimiter) throws MalformedCommandException {
		boolean isQuotePending = false;
		String cl2 = cl.trim();
		Vector outV = new Vector();
		char[] c = cl2.toCharArray();
		StringBuffer currentArg = new StringBuffer();
		for (int i = 0; i < c.length; i++) {
			if (isQuotePending) {
				// look for the next quote
				if (c[i] == quoteDelimiter) {
					// close off the current arg, start on the next.
					outV.addElement(currentArg.toString());
					currentArg = new StringBuffer();
					isQuotePending = false;
				} else {
					currentArg.append(c[i]);
				}
			} else {
				// so no quote is pending.
				if (c[i] == ' ' || c[i] == quoteDelimiter) {
					// we have no quote and have a blank, so tokenize on that
					outV.addElement(currentArg.toString());
					currentArg = new StringBuffer();
					isQuotePending = (c[i] == quoteDelimiter);
				} else {
					currentArg.append(c[i]);
				}
			}
		} //end i - for
		outV.addElement(currentArg.toString());
		if (outV.size() > 0) {
			String temp = (String) outV.elementAt(0);
			temp = temp.toLowerCase();
			outV.setElementAt(temp,0);
			// Now we loop through everything else and get rid of extra blank
			// or empty tokens. These can occur when the user enters multiple
			// blanks between commands.
			for (int j = 0; j < outV.size(); j++) {
				temp = (String) outV.elementAt(j);
				if (temp == null || temp.length() == 0) {
					// remove it, adjust indices.
					outV.removeElementAt(j);
					j = j - 1;
				}
			} //end other j - for
		}
		isQuotePending = false; // just to be sure.
		return outV;
	}
}