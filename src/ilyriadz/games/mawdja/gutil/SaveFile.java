
package ilyriadz.games.mawdja.gutil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class SaveFile
{
    private final Map<String, SaveFileData> map = new HashMap<>();
    
    private final int layers;
    private final int rows;
    private final int columns;
    private RandomAccessFile raf;
    
    public static class SaveFileData
    {
        public final String name;
        public final int layer;
        public final int row;
        public final int column;

        public SaveFileData(String name, int layer, int row, int column)
        {
            this.name = name;
            this.layer = layer;
            this.row = row;
            this.column = column;
            
            
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + this.layer;
            hash = 53 * hash + this.row;
            hash = 53 * hash + this.column;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SaveFileData other = (SaveFileData) obj;
            if (this.layer != other.layer) {
                return false;
            }
            if (this.row != other.row) {
                return false;
            }
            return this.column == other.column;
        }

        @Override
        public String toString() {
            return "SaveFileData{" + "layer=" + layer + ", row=" + row + ", column=" + column + '}';
        }      
    };
    
    public SaveFile(String name, int layers, int rows, int columns) 
    {
        validateConstructorIndexes(layers, rows, columns);
        this.layers = layers >= 1 ? layers : 1;
        this.rows = rows;
        this.columns = columns;
        initialize(name);
    }
    
    private void initialize(String name)         
    {
        if (!Files.exists(Paths.get(name)))
        {
            try 
            {
                raf = new RandomAccessFile(name, "rw");
                for (int i = 0; i < layers; i++) 
                {
                    raf.write(new byte[rows * columns]);
                } // end for
            }  // end try
            catch (FileNotFoundException ex) 
            {
                Logger.getLogger(SaveFile.class.getName()).log(
                    Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
            } // end catch
        } // end if
        else
        {
            try 
            {
                raf = new RandomAccessFile(name, "rw");
            } // endd try
            catch (FileNotFoundException ex) 
            {
                Logger.getLogger(SaveFile.class.getName()).log(
                    Level.SEVERE, null, ex);
            } // end catch
        }
    }
    
    private void validateConstructorIndexes(int layers, int rows, int columns)
    {
        if (layers <= 0 || rows <= 0 || columns < 8)
            throw new IllegalArgumentException(String.format(
                "arguments layers[1-n]:%d, rows[1-n]:%d, column[8-n]:%d",
                    layers, rows, columns));
    } // end method
    
    private void validateIndexBounds(int layer, int row)
    {
        if (layer < 0 || layer >= layers || row < 0 || row >= rows)
            throw new IndexOutOfBoundsException(String.format(
                "arguments layer[0-%d]:%d, row[0-%d]:%d",
                    layers - 1, layer, rows - 1, row));
    } // end method
    
    private void validateIndexBounds(int column)
    {
        if (column < 0 || column >= columns)
            throw new IndexOutOfBoundsException(String.format(
                "arguments column[0-%d]:%d", columns - 1, column));
    } // end method
    
    private int layerPosition(int layer, int row)
    {
        return layer * rows * columns + row * columns;
    } // end method
    
    public int columnSize()
    {
        return columns;
    } // end method
    
    public void map(String name, int layer, int row)
    {
        validateMappedNameExist(name);
        validateIndexBounds(layer, row);
        validateReserved(layer, row);
        map.put(name, new SaveFileData(name, layer, row, 0));
    }
    
    private void validateMappedNameNotExist(String name)
    {
        if (!map.containsKey(name))
            throw new IllegalArgumentException(
                "\"" + name + "\""  + " mapped name not exist");
    }
    
    private void validateMappedNameExist(String name)
    {
        if (map.containsKey(name))
            throw new IllegalArgumentException("\"" + name + "\"" + " exists as key in " +
                    this + " map");
    }
    
    public final boolean mappedNameExist(String name)
    {
        return map.containsKey(name);
    }
    
    private void validateReserved(int layer, int row)
    {
        map.values().stream()
                .filter((v) -> (v.layer == layer && v.row == row))
                .forEachOrdered((_item) -> {
                    throw new IllegalArgumentException(
                        String.format(
                            "row %d of layer %d already reserved for \"%s\"",
                            row, layer, nameOf(layer, row)));
        }); // end for
    }
    
    private String nameOf(int layer, int row)
    {
        return map.values().stream()
                .filter(v -> v.layer == layer && v.row == row)
                .map(v -> v.name)
                .findAny().orElse("");
    }
    
    public byte get(String name, int column)
    {
        validateMappedNameNotExist(name);
        validateIndexBounds(column);
        var data = map.get(name);

        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_ONLY,
                    layerPosition(data.layer, data.row), columnSize());
            return buf.get(column);
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        throw new UnknownError();
    }
    
    public void put(String name, int column, byte value)
    {
        validateMappedNameNotExist(name);
        validateIndexBounds(column);
        var data = map.get(name);

        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_WRITE,
                    layerPosition(data.layer, data.row), columnSize());
            buf.put(column, value);
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean getBoolean(String name, int column)
    {
        validateMappedNameNotExist(name);
        validateIndexBounds(column);
        var data = map.get(name);

        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_ONLY,
                    layerPosition(data.layer, data.row), columnSize());
            return buf.get(column) != 0;
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        throw new UnknownError();
    }
    
    public void putBoolean(String name, int column, boolean value)
    {
        validateMappedNameNotExist(name);
        validateIndexBounds(column);
        var data = map.get(name);

        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_WRITE,
                    layerPosition(data.layer, data.row), columnSize());
            buf.put(column, value ? (byte)1 : (byte)0);
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private byte[] getBytes(int layer, int row)
    {
        validateIndexBounds(layer, row);
        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_ONLY,
                    layerPosition(layer, row), columnSize());
            var bytes = new byte[columnSize()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = buf.get(i);
            }
            return bytes;
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        throw new UnknownError();
    } //*/
    
    public byte[] getBytes(String name)
    {
        validateMappedNameNotExist(name);
        var data = map.get(name);

        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_ONLY,
                    layerPosition(data.layer, data.row), columnSize());
            var bytes = new byte[columnSize()];
            for (int i = 0; i < bytes.length; i++) 
                bytes[i] = buf.get(i);
            return bytes;
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        throw new UnknownError();
    }
    
    public void putBytes(String name, byte... bytes)
    {
        validateMappedNameNotExist(name);
        var data = map.get(name);

        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_WRITE,
                    layerPosition(data.layer, data.row), columnSize());
            for (int i = 0; i < columnSize(); i++) 
            {
                if (i >= bytes.length)
                    buf.put(i, (byte)0);
                else
                    buf.put(i, bytes[i]);
            } // end for
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*public boolean[] getBooleans(int layer, int row)
    {
        validateIndexBounds(layer, row);
        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_ONLY,
                    layerPosition(layer, row), columnSize());
            var bytes = new boolean[columnSize()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = buf.get(i) != 0;
            }
            return bytes;
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        throw new UnknownError();
    }*/
    
    public boolean[] getBooleans(String name)
    {
        validateMappedNameNotExist(name);
        var data = map.get(name);

        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_ONLY,
                    layerPosition(data.layer, data.row), columnSize());
            var booleans = new boolean[columnSize()];
            for (int i = 0; i < booleans.length; i++) 
                booleans[i] = buf.get(i) != 0;
            return booleans;
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        throw new UnknownError();
    }
    
    public void putBooleans(String name, boolean... booleans)
    {
        validateMappedNameNotExist(name);
        var data = map.get(name);

        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_WRITE,
                    layerPosition(data.layer, data.row), columnSize());
            for (int i = 0; i < columnSize(); i++) 
            {
                if (i >= booleans.length)
                    buf.put(i, (byte)0);
                else
                    buf.put(i, booleans[i] ? (byte)1 : (byte)0);
            } // end for
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getInt(String name)
    {
        validateMappedNameNotExist(name);
        var data = map.get(name);

        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_ONLY,
                    layerPosition(data.layer, data.row), columnSize());
            return buf.getInt(0);
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        throw new UnknownError();
    }
    
    public void putInt(String name, int value)
    {
        validateMappedNameNotExist(name);
        var data = map.get(name);

        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_WRITE,
                    layerPosition(data.layer, data.row), columnSize());
            buf.putInt(0, value);
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public long getLong(String name)
    {
        validateMappedNameNotExist(name);
        var data = map.get(name);

        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_ONLY,
                    layerPosition(data.layer, data.row), columnSize());
            return buf.getLong(0);
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        throw new UnknownError();
    }
    
    public void putLong(String name, long value)
    {
        validateMappedNameNotExist(name);
        var data = map.get(name);

        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_WRITE,
                    layerPosition(data.layer, data.row), columnSize());
            buf.putLong(0, value);
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public double getDouble(String name)
    {
        validateMappedNameNotExist(name);
        var data = map.get(name);

        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_ONLY,
                    layerPosition(data.layer, data.row), columnSize());
            return buf.getDouble(0);
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        throw new UnknownError();
    }
    
    public void putDouble(String name, double value)
    {
        validateMappedNameNotExist(name);
        var data = map.get(name);

        try {
            var buf = raf.getChannel().map(FileChannel.MapMode.READ_WRITE,
                    layerPosition(data.layer, data.row), columnSize());
            buf.putDouble(0, value);
        } catch (IOException ex) {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getString(String name)
    {
        validateMappedNameNotExist(name);
        
        return new String(getBytes(name), StandardCharsets.UTF_16).strip();
    }
    
    public void putString(String name, String str)
    {
        validateMappedNameNotExist(name);
        putBytes(name, str.getBytes(StandardCharsets.UTF_16));
    }
    
    public void putObject(String name, Object obj)
    {
        var byteArrayOutputStream =
                new ByteArrayOutputStream();
        
        try(var out = new ObjectOutputStream(byteArrayOutputStream))
        {
            out.writeObject(obj);
            out.flush();
            putBytes(name, byteArrayOutputStream.toByteArray());
        } // end try
        catch (IOException ex) 
        {
            Logger.getLogger(SaveFile.class.getName()).log(Level.SEVERE, null, ex);
        } // end catch
    }
    
    public Object getObject(String name)
    {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(getBytes(name));
        try(var in = new ObjectInputStream(byteArrayInputStream))
        {
            return in.readObject();
        }  // end try
        catch (IOException | ClassNotFoundException ex) 
        {
            ex.printStackTrace();
            Logger.getLogger(SaveFile.class.getName()).log(
                Level.SEVERE, "no object found!");
        }
        // end catch
         // end catch
        
        return null;
    } // end method

    @Override
    public String toString() {
        for (int i = 0; i < layers; i++) 
        {
            System.out.println("layer#" + i);
            for (int j = 0; j < rows; j++) {
                System.out.println(j + ":"+ Arrays.toString(getBytes(i, j)));
            }
        }
        return "";
    }    
}
