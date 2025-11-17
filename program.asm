.text
.globl main

factorial:
        pushq   %rbp
        movq    %rsp, %rbp
        subq    $16, %rsp
        movq    %rdi, -8(%rbp)
        movq    -8(%rbp), %rax
        pushq   %rax
        movq    $0, %rax
        popq    %rcx
        cmpq    %rax, %rcx
        sete    %al
        movzbq  %al, %rax
        cmpq    $0, %rax
        je      L_else_0
        movq    $1, %rax
        leave
        ret
        jmp     L_end_1
L_else_0:
        movq    -8(%rbp), %rax
        pushq   %rax
        # Preparando llamada a factorial
        movq    -8(%rbp), %rax
        pushq   %rax
        movq    $1, %rax
        popq    %rcx
        subq    %rax, %rcx
        movq    %rcx, %rax
        movq    %rax, %rdi
        andq    $-16, %rsp
        call    factorial
        popq    %rcx
        imulq   %rcx, %rax
        leave
        ret
L_end_1:

main:
        pushq   %rbp
        movq    %rsp, %rbp
        subq    $16, %rsp
        # Preparando llamada a factorial
        movq    $5, %rax
        movq    %rax, %rdi
        andq    $-16, %rsp
        call    factorial
        movq    %rax, -8(%rbp)
        movq    -8(%rbp), %rax
        leave
        ret

