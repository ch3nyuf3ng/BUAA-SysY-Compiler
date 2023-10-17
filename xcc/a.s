# mips code of 'a.c'
.data 0x10000000
.word
10,2,3,4
fs_1: .asciiz ")="
fs_2: .asciiz "\n"
fs_0: .asciiz "fib("
.text
entry$:
	jal main
	li $v0,10
	syscall
fib:
	addiu $sp,$sp,-52
	sw $a0,48($sp)
	sw $ra,0($sp)
fib_L1:
	lw $t2,48($sp)
	sw $t2,44($sp)
	lw $t0,44($sp)
	slti $t2,$t0,0x2
	sw $t2,40($sp)
	lw $t0,40($sp)
	beq $t0,$zero,fib_L3
fib_L2:
	ori $v0,$zero,0x1
	j fib_L6
fib_L3:
	lw $t2,48($sp)
	sw $t2,36($sp)
	lw $t0,36($sp)
	xori $t2,$t0,0x2
	sw $t2,32($sp)
	lw $t0,32($sp)
	sltiu $t2,$t0,0x1
	sw $t2,32($sp)
	lw $t0,32($sp)
	beq $t0,$zero,fib_L5
fib_L4:
	ori $v0,$zero,0x2
	j fib_L6
fib_L5:
	lw $t2,48($sp)
	sw $t2,28($sp)
	lw $t0,28($sp)
	addiu $t2,$t0,-1
	sw $t2,24($sp)
	lw $t0,24($sp)
	addu $a0,$t0,$zero
	jal fib
	addu $t2,$v0,$zero
	sw $t2,20($sp)
	lw $t2,48($sp)
	sw $t2,16($sp)
	lw $t0,16($sp)
	addiu $t2,$t0,-2
	sw $t2,12($sp)
	lw $t0,12($sp)
	addu $a0,$t0,$zero
	jal fib
	addu $t2,$v0,$zero
	sw $t2,8($sp)
	lw $t0,20($sp)
	lw $t1,8($sp)
	addu $t2,$t0,$t1
	sw $t2,4($sp)
	lw $t0,4($sp)
	addu $v0,$t0,$zero
	j fib_L6
fib_L6:
	lw $ra,0($sp)
	addiu $sp,$sp,52
	jr $ra
deref_array_addr:
	addiu $sp,$sp,-32
	sw $a0,28($sp)
deref_array_addr_L1:
	lw $t2,28($sp)
	sw $t2,24($sp)
	ori $t2,$zero,0x0
	sw $t2,20($sp)
	lw $t0,24($sp)
	lw $t1,20($sp)
	addu $t2,$t0,$t1
	sw $t2,16($sp)
	lw $t0,16($sp)
	addu $t2,$t0,$zero
	sw $t2,12($sp)
	ori $t2,$zero,0x0
	sw $t2,8($sp)
	lw $t0,12($sp)
	lw $t1,8($sp)
	addu $t2,$t0,$t1
	sw $t2,4($sp)
	lw $t0,4($sp)
	lw $t2,0($t0)
	sw $t2,0($sp)
	lw $t0,0($sp)
	addu $v0,$t0,$zero
	j deref_array_addr_L2
deref_array_addr_L2:
	addiu $sp,$sp,32
	jr $ra
main:
	addiu $sp,$sp,-32
	sw $ra,0($sp)
main_L1:
	addiu $t0,$gp,-32768
	addu $t2,$t0,$zero
	sw $t2,20($sp)
	lw $t0,20($sp)
	addu $a0,$t0,$zero
	jal deref_array_addr
	addu $t2,$v0,$zero
	sw $t2,16($sp)
	lw $t1,16($sp)
	sw $t1,28($sp)
	lw $t2,28($sp)
	sw $t2,12($sp)
	lw $t2,28($sp)
	sw $t2,8($sp)
	lw $t0,8($sp)
	addu $a0,$t0,$zero
	jal fib
	addu $t2,$v0,$zero
	sw $t2,4($sp)
	la $a0,fs_0
	ori $v0,$zero,0x4
	syscall
	ori $v0,$zero,0x1
	lw $t0,12($sp)
	addu $a0,$t0,$zero
	syscall
	la $a0,fs_1
	ori $v0,$zero,0x4
	syscall
	ori $v0,$zero,0x1
	lw $t0,4($sp)
	addu $a0,$t0,$zero
	syscall
	la $a0,fs_2
	ori $v0,$zero,0x4
	syscall
	ori $v0,$zero,0x0
	j main_L2
main_L2:
	lw $ra,0($sp)
	addiu $sp,$sp,32
	jr $ra
