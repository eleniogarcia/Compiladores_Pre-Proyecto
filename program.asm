.text
.globl main

suma:
        pushq   %rbp
        movq    %rsp, %rbp
        subq    $16, %rsp
        movq    %rdi, -8(%rbp)
        movq    %rsi, -16(%rbp)
        movq    -8(%rbp), %rax
        pushq   %rax
        movq    -16(%rbp), %rax
        popq    %rcx
        addq    %rcx, %rax
        leave
        ret

main:
        pushq   %rbp
        movq    %rsp, %rbp
        subq    $16, %rsp
        movq    $4, %rax
        movq    %rax, -8(%rbp)
        movq    $2, %rax
        movq    %rax, -16(%rbp)
        # Preparando llamada a suma
        movq    -8(%rbp), %rax
        movq    %rax, %rdi
        movq    -16(%rbp), %rax
        pushq   %rax
        popq    %rsi
        andq    $-16, %rsp
        call    suma
        movq    %rax, -8(%rbp)
        movq    $0, %rax
        leave
        ret

