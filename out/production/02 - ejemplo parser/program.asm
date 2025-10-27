main:
        pushq   %rbp
        movq    %rsp, %rbp
        subq    $24, %rsp
        movq    -8(%rbp), %rax
        pushq   %rax
        movq    $3, %rax
        pushq   %rax
        movq    $2, %rax
        popq    %rcx
        imulq   %rcx, %rax
        pushq   %rax
        movq    -24(%rbp), %rax
        popq    %rcx
        imulq   %rcx, %rax
        popq    %rcx
        addq    %rcx, %rax
        movq    %rax, -16(%rbp)
        movq    -8(%rbp), %rax
        pushq   %rax
        movq    -24(%rbp), %rax
        popq    %rcx
        cmpq    $0, %rax
        je      L_else_0
        movq    $1, %rax
        leave
        ret
        jmp     L_end_1
        L_else_0:
        movq    $2, %rax
        leave
        ret
        L_end_1:
        movq    $0, %rax
        leave
        ret
