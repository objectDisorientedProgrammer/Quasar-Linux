/*
 * Created: May 28, 2013
 * 
    The MIT License (MIT)
    
    Copyright (c) 2013 Gamma (Douglas Chidester, James Howard, Steve Corbette)
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
 */

package quasar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

public class EntryController
{
	private boolean DEBUG_PRINT = false;
    private int documentCount;
    private int websiteCount;
    private int pictureCount;
    private int contactCount;
    private int totalCount;
    private String dataFile;
    private ArrayList<Data> dataContainer = new ArrayList<Data>(20);
    private String[] items;

    /**
     * Creates a EntryController with a specified load/save file.
     * @param dataFile - filename for the loading/saving
     */
    public EntryController(String dataFile) {
        super();
        this.dataFile = dataFile;
        initializeVariables();
        
        if(this.dataFile != null || !this.dataFile.isEmpty())
        {
            try {
                loadFile(this.dataFile);
            } catch (IOException e) {
                System.err.println("EntryController(String) - " + e.getMessage());
            }
        }
        
    }

    /**
     * Reset counts and set up head/tail nodes.
     */
    private void initializeVariables() {
        this.documentCount = 0;
        this.websiteCount = 0;
        this.pictureCount = 0;
        this.contactCount = 0;
        this.totalCount = 0;
    }

    /**
     * 
     * @param String
     * @throws IOException
     */
    public void loadFile(String filename) throws IOException {
        String file = FileUtils.readFileToString(new File(filename));//FileUtils.readFileToString(new File(filename), "UTF-8");
        String[] fileLines = file.split("\n");
        
        for(String line : fileLines)
        {
            String[] tokens = line.split("¶");
            Data d;
            
            // TODO this is a mess but works for now.
            // somehow initialize the data object and set the common attributes...
            switch(Integer.parseInt(tokens[0]))
            {
            case Quasar.DOCUMENT: // doc
                d = new Document(tokens[6]);
                d.setTitle(tokens[1]);
                d.setDescription(tokens[2]);
                d.setDate(tokens[3]);
                d.setKeywords(tokens[4]);
                d.setType(tokens[5]);
                
                if(DEBUG_PRINT)
                    System.out.println("Adding " + d.toString());
                
                addEntry(d);
                break;
            case Quasar.WEBSITE: // web
                d = new Website(tokens[6]);
                d.setTitle(tokens[1]);
                d.setDescription(tokens[2]);
                d.setDate(tokens[3]);
                d.setKeywords(tokens[4]);
                d.setType(tokens[5]);
                
                if(DEBUG_PRINT)
                    System.out.println("Adding " + d.toString());
                
                addEntry(d);
                break;
            case Quasar.PICTURE: // pic
                d = new Picture(tokens[6]);
                d.setTitle(tokens[1]);
                d.setDescription(tokens[2]);
                d.setDate(tokens[3]);
                d.setKeywords(tokens[4]);
                d.setType(tokens[5]);
                
                if(DEBUG_PRINT)
                    System.out.println("Adding " + d.toString());
                
                addEntry(d);
                
                break;
            case Quasar.CONTACT: // contact
                d = new Contact(tokens[6], tokens[7], tokens[8], tokens[9]);
                d.setTitle(tokens[1]);
                d.setDescription(tokens[2]);
                d.setDate(tokens[3]);
                d.setKeywords(tokens[4]);
                d.setType(tokens[5]);
                
                if(DEBUG_PRINT)
                    System.out.println("Adding " + d.toString());
                
                addEntry(d);
                break;
            default:
            	if(DEBUG_PRINT)
                    System.out.println("Unknown: " + line);
                break;
            }
        }
    }

    /**
     * 
     * @param String
     */
    public void saveToFile() {
        throw new UnsupportedOperationException();
    }
    
    public Data createNewEntry()
    {
        Data d = new Data();
        dataContainer.add(d);
        return dataContainer.get(dataContainer.size() - 1);
    }
    
    public void addEntry(Data d)
    {
        dataContainer.add(d);
    }

    public boolean isEmpty() {
        return dataContainer.isEmpty();
    }
    
    public Data[] getAllData()
    {
        Data ary[] = new Data[dataContainer.size()];
        int i = 0;
        for(Data d : dataContainer)
        {
            ary[i] = d;
            ++i;
        }
        return ary;
    }
    
    public String[] getAllDataTitles()
    {
        items = new String[dataContainer.size()];
        int i = 0;
        
        for (Data data : dataContainer)
        {
            items[i] = data.title;
            ++i;
        }
        
        return items;
    }

    /**
     * Get an entry by title string.
     * @param title Entry to look up.
     * @return Data object or {@code null} if not found.
     */
    public Data getEntry(String title)
    {
        for (Data data : dataContainer)
        {
            if(data.title.equalsIgnoreCase(title))
            {
                return data;
            }
        }
        return null;
    }
    
    /**
     * Get an entry by index.
     * @param index
     * @return Data object.
     */
    public Data getEntry(int index)
    {
        return dataContainer.get(index);
    }
    
    public boolean search(String searchString, int filter, Vector<Data> results)
    {
        String lookup = Quasar.entryTypeStrings[filter].toLowerCase().substring(0, 1) ;
        
        for(Data data : dataContainer)
        {
            if (filter == Quasar.ALL)
            {
                // gather all entries
                if (searchString.equalsIgnoreCase(""))
                    results.add(data);
                //search only titles
                else if (data.getTitle().contains(searchString))
                {
                    results.add(data);
                }
            }
            else
            {
                //search by type, then by title
//                if (data.getType() == filter) FIXME type needs to become an int
                if (lookup.equalsIgnoreCase(data.getType()))
                {
                    // if blank search, gather all entries of the type
                    if (searchString.equalsIgnoreCase(""))
                    {
                        results.add(data);
                    }
                    // gather all entries that match the query text
                    else if (data.getTitle().contains(searchString))
                    {
                        results.add(data);
                    }
                }
            }
        }
        
        return results.size() > 0;
    }

    public Data getEntry(Data selectedValue)
    {
        for(Data d : dataContainer)
        {
            if(d.equals(selectedValue))
                return d;
        }
        return null;
    }
}
