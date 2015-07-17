java=/usr/lib/jvm/java-7-openjdk-i386/bin/java
javac=/usr/lib/jvm/java-7-openjdk-i386/bin/javac

run: F1.class F2.class F3.class F4.class F5.class libcoro/C1.exe
	@${java} F1 1500
	@${java} F2 1500
	@${java} F3 1500
	@${java} F4 1500

	@${java} F1 80
	@${java} F2 80
	@${java} F3 80
	@${java} F4 80

	@echo

	@./libcoro/C1.exe 1500
	@./libcoro/C2.exe 1500
	@./libcoro/C3.exe 1500
	@./libcoro/C4.exe 1500

	@./libcoro/C1.exe 80
	@./libcoro/C2.exe 80
	@./libcoro/C3.exe 80
	@./libcoro/C4.exe 80

# -O2 inlines away the trivial function calls, so we don't use it.
libcoro/C1.exe:
	gcc libcoro/C1.c libcoro/coro.c -lrt -o libcoro/C1.exe
	gcc libcoro/C2.c libcoro/coro.c -lrt -o libcoro/C2.exe
	gcc libcoro/C3.c libcoro/coro.c -lrt -o libcoro/C3.exe
	gcc libcoro/C4.c libcoro/coro.c -lrt -o libcoro/C4.exe



#	echo ${java} F5

run1: F1.class
	${java} F1 1500
	${java} F1 80
run5: F5.class
	${java} F5

run3: F3.class
	${java} F3
run4: F4.class
	${java} F4

F1.class: F1.java
	${javac} F1.java

F2.class: F2.java
	${javac} F2.java
F5.class: F5.java
	${javac} F5.java
F3.class: F3.java
	${javac} F3.java
F4.class: F4.java
	${javac} F4.java
