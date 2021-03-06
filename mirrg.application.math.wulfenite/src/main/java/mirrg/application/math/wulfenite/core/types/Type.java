package mirrg.application.math.wulfenite.core.types;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;

import mirrg.helium.math.hydrogen.complex.StructureComplex;

public class Type<T>
{

	public static final Type<SlotInteger> INTEGER = new Type<>(SlotInteger.class, SlotInteger::new, "int");
	public static final Type<SlotDouble> DOUBLE = new Type<>(SlotDouble.class, SlotDouble::new, "double");
	public static final Type<StructureComplex> COMPLEX = new Type<>(StructureComplex.class, StructureComplex::new, "complex");
	public static final Type<SlotBoolean> BOOLEAN = new Type<>(SlotBoolean.class, SlotBoolean::new, "boolean");
	public static final Type<SlotString> STRING = new Type<>(SlotString.class, SlotString::new, "string");
	public static final Type<SlotColor> COLOR = new Type<>(SlotColor.class, SlotColor::new, "color");

	public static final Type<SlotInteger> I = INTEGER;
	public static final Type<SlotDouble> D = DOUBLE;
	public static final Type<StructureComplex> C = COMPLEX;
	public static final Type<SlotBoolean> B = BOOLEAN;
	public static final Type<SlotString> S = STRING;
	public static final Type<SlotColor> Co = COLOR;

	private static ArrayList<Type<?>> types = new ArrayList<>();
	static {
		types.add(INTEGER);
		types.add(DOUBLE);
		types.add(COMPLEX);
		types.add(BOOLEAN);
		types.add(STRING);
		types.add(COLOR);
	}

	public static Optional<Type<?>> getType(String name)
	{
		return types.stream()
			.filter(t -> t.name.toLowerCase().equals(name))
			.findFirst();
	}

	public final Class<T> type;
	private final Supplier<T> supplier;
	public final String name;

	public Type(Class<T> type, Supplier<T> supplier, String name)
	{
		this.type = type;
		this.supplier = supplier;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public T create()
	{
		return supplier.get();
	}

}
