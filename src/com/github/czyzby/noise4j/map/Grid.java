package com.github.czyzby.noise4j.map;

import java.util.Arrays;

/** A float array wrapper. Allows to use a single 1D float array as a 2D array.
 *
 * @author MJ */
public class Grid implements Cloneable {
    private final float[] grid;
    private final int width;
    private final int height;

    /** @param size amount of columns and rows. */
    public Grid(final int size) {
        this(size, size);
    }

    /** @param width amount of columns.
     * @param height amount of rows. */
    public Grid(final int width, final int height) {
        this(new float[width * height], width, height);
    }

    /** @param initialValue all cells will start with this value.
     * @param width amount of columns.
     * @param height amount of rows. */
    public Grid(final float initialValue, final int width, final int height) {
        this(new float[width * height], width, height);
        set(initialValue);
    }

    /** @param grid array that will internally used by the grid. Its size has to be equal width multiplied by height.
     * @param width amount of columns.
     * @param height amount of rows. */
    public Grid(final float[] grid, final int width, final int height) {
        this.grid = grid;
        this.width = width;
        this.height = height;
        if (grid.length != width * height) {
            throw new IllegalArgumentException("Array with length: " + grid.length
                    + " is too small or too big to store a grid with " + width + " columns and " + height + " rows.");
        }
    }

    /** @return direct reference to the stored array. Use in extreme cases, try to use getters instead. */
    public float[] getArray() {
        return grid;
    }

    /** @return amount of columns. */
    public int getWidth() {
        return width;
    }

    /** @return amount of rows. */
    public int getHeight() {
        return height;
    }

    /** @param x column index.
     * @param y row index.
     * @return value stored in the chosen cell. */
    public float get(final int x, final int y) {
        return grid[toIndex(x, y)];
    }

    /** @param x column index.
     * @param y row index.
     * @return true if the coordinates are valid and can be safely used with methods like {@link #get(int, int)}. */
    public boolean isIndexValid(final int x, final int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /** @param x column index.
     * @param y row index.
     * @param value will be set as the value in the chosen cell.
     * @return value (parameter), for chaining. */
    public float set(final int x, final int y, final float value) {
        return grid[toIndex(x, y)] = value;
    }

    /** @param x column index.
     * @param y row index.
     * @param value will be added to the current value stored in the chosen cell.
     * @return current cell value after adding the passed parameter. */
    public float add(final int x, final int y, final float value) {
        return grid[toIndex(x, y)] += value;
    }

    /** @param x column index.
     * @param y row index.
     * @param value will be subtracted from the current value stored in the chosen cell.
     * @return current cell value after subtracting the passed parameter. */
    public float subtract(final int x, final int y, final float value) {
        return grid[toIndex(x, y)] -= value;
    }

    /** @param x column index.
     * @param y row index.
     * @param value will be multiplied by the current value stored in the chosen cell.
     * @return current cell value after multiplying by the passed parameter. */
    public float multiply(final int x, final int y, final float value) {
        return grid[toIndex(x, y)] *= value;
    }

    /** @param x column index.
     * @param y row index.
     * @param value will divide the current value stored in the chosen cell.
     * @return current cell value after dividing by the passed parameter. */
    public float divide(final int x, final int y, final float value) {
        return grid[toIndex(x, y)] /= value;
    }

    /** @param x column index.
     * @param y row index.
     * @param mod will be used to perform modulo operation on the current cell value.
     * @return current cell value after modulo operation. */
    public float modulo(final int x, final int y, final float mod) {
        return grid[toIndex(x, y)] %= mod;
    }

    /** @param x column index.
     * @param y row index.
     * @return actual array index of the cell.
     * @see #getArray() */
    public int toIndex(final int x, final int y) {
        return x + y * width;
    }

    /** @param index actual array index of a cell.
     * @return column index.
     * @see #getArray() */
    public int toX(final int index) {
        return index % width;
    }

    /** @param index actual array index of a cell.
     * @return row index.
     * @see #getArray() */
    public int toY(final int index) {
        return index / width;
    }

    /** Iterates over the whole grid.
     *
     * @param cellConsumer will consume each cell. If returns true, further iteration will be cancelled. */
    public void forEach(final CellConsumer cellConsumer) {
        iterate(cellConsumer, 0, grid.length);
    }

    /** Iterates over the grid from a starting point.
     *
     * @param cellConsumer will consume each cell. If returns true, further iteration will be cancelled.
     * @param fromX first cell column index. Min is 0.
     * @param fromY first cell row index. Min is 0. */
    public void forEach(final CellConsumer cellConsumer, final int fromX, final int fromY) {
        iterate(cellConsumer, toIndex(fromX, fromY), grid.length);
    }

    /** Iterates over chosen cells range in the grid.
     *
     * @param cellConsumer will consume each cell. If returns true, further iteration will be cancelled.
     * @param fromX first cell column index. Min is 0.
     * @param fromY first cell row index. Min is 0.
     * @param toX last cell column index (excluded). Max is {@link #getWidth()}.
     * @param toY last cell row index (excluded). Max is {@link #getHeight()}. */
    public void forEach(final CellConsumer cellConsumer, final int fromX, final int fromY, final int toX,
            final int toY) {
        iterate(cellConsumer, toIndex(fromX, fromY), toIndex(toX, toY));
    }

    /** @param cellConsumer will consume each cell. If returns true, further iteration will be cancelled.
     * @param fromIndex actual
     * @param toIndex */
    protected void iterate(final CellConsumer cellConsumer, final int fromIndex, final int toIndex) {
        for (int index = fromIndex; index < toIndex; index++) {
            if (cellConsumer.consume(this, toX(index), toY(index), grid[index])) {
                break;
            }
        }
    }

    /** @param grid its values will replace this grid's values. */
    public void set(final Grid grid) {
        validateGrid(grid);
        System.arraycopy(grid.grid, 0, this.grid, 0, this.grid.length);
    }

    /** @param grid its values will be added to this grid's values. */
    public void add(final Grid grid) {
        validateGrid(grid);
        for (int index = 0, length = this.grid.length; index < length; index++) {
            this.grid[index] += grid.grid[index];
        }
    }

    /** @param grid its values will be subtracted from this grid's values. */
    public void subtract(final Grid grid) {
        validateGrid(grid);
        for (int index = 0, length = this.grid.length; index < length; index++) {
            this.grid[index] -= grid.grid[index];
        }
    }

    /** @param grid its values will multiply this grid's values. */
    public void multiply(final Grid grid) {
        validateGrid(grid);
        for (int index = 0, length = this.grid.length; index < length; index++) {
            this.grid[index] *= grid.grid[index];
        }
    }

    /** @param grid its values will be used to divide this grid's values. */
    public void divide(final Grid grid) {
        validateGrid(grid);
        for (int index = 0, length = this.grid.length; index < length; index++) {
            this.grid[index] /= grid.grid[index];
        }
    }

    /** @param grid will be validated.
     * @throws IllegalStateException if sizes do not match. */
    protected void validateGrid(final Grid grid) {
        if (grid.width != width || grid.height != height) {
            throw new IllegalStateException("Grid's sizes do not match. Unable to perform operation.");
        }
    }

    /** Sets all values in the map.
     *
     * @param value will be set.
     * @return this, for chaining. */
    public Grid set(final float value) {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index] = value;
        }
        return this;
    }

    /** Increases all values in the map.
     *
     * @param value will be added.
     * @return this, for chaining. */
    public Grid add(final float value) {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index] += value;
        }
        return this;
    }

    /** Decreases all values in the map.
     *
     * @param value will be subtracted.
     * @return this, for chaining. */
    public Grid subtract(final float value) {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index] -= value;
        }
        return this;
    }

    /** Multiplies all values in the map.
     *
     * @param value will be used.
     * @return this, for chaining. */
    public Grid multiply(final float value) {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index] *= value;
        }
        return this;
    }

    /** Divides all values in the map.
     *
     * @param value will be used.
     * @return this, for chaining. */
    public Grid divide(final float value) {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index] /= value;
        }
        return this;
    }

    /** Performs modulo operation on all values in the map.
     *
     * @param modulo will be used.
     * @return this, for chaining. */
    public Grid modulo(final float modulo) {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index] %= modulo;
        }
        return this;
    }

    /** Negates all values in the map.
     *
     * @return this, for chaining. */
    public Grid negate() {
        for (int index = 0, length = grid.length; index < length; index++) {
            grid[index] = -grid[index];
        }
        return this;
    }

    @Override
    public boolean equals(final Object object) {
        return object == this || object instanceof Grid && ((Grid) object).width == width // If width is equal
                && Arrays.equals(((Grid) object).grid, grid); // and arrays are the same size, height is equal.
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(grid);
    }

    @Override
    protected Object clone() {
        return new Grid(grid.clone(), width, height);
    }

    /** Alias for {@link #clone()} with casted result.
     *
     * @return a new instance of the grid with same size and cell values. */
    public Grid copy() {
        return new Grid(grid.clone(), width, height);
    }

    @Override
    public String toString() {
        final StringBuilder logger = new StringBuilder();
        forEach(new CellConsumer() {
            @Override
            public boolean consume(final Grid grid, final int x, final int y, final float value) {
                logger.append('[').append(x).append(',').append(y).append('|').append(value).append(']');
                if (x == grid.width - 1) {
                    logger.append('\n');
                } else {
                    logger.append(' ');
                }
                return CONTINUE;
            }
        });
        return logger.toString();
    }

    /** Allows to perform an action on {@link Grid}'s cells.
     *
     * @author MJ */
    public static interface CellConsumer {
        /** Should be returned by {@link #consume(Grid, int, int, float)} method for code clarity. */
        boolean BREAK = true, CONTINUE = false;

        /** @param grid contains the cell.
         * @param x column index of the current cell.
         * @param y row index of the current cell.
         * @param value value stored in the current cell.
         * @return if true and {@link CellConsumer} is used to iterate over the {@link Grid} using an iteration method
         *         like {@link Grid#forEach(CellConsumer)}, further iteration will be cancelled.
         * @see #BREAK
         * @see #CONTINUE */
        public boolean consume(Grid grid, int x, int y, float value);
    }
}