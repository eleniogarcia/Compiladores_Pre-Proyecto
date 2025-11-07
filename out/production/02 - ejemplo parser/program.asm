main:
        pushq   %rbp
        movq    %rsp, %rbp
        subq    $8, %rsp
        movq    $9, %rax
        movq    %rax, -8(%rbp)
        movq    -8(%rbp), %rax
        pushq   %rax
        movq    $4, %rax
        popq    %rcx
        cmpq    %rax, %rcx
        setg    %al
        movzbq  %al, %rax
        cmpq    $0, %rax
        je      L_else_0
        movq    $5, %rax
        movq    %rax, -8(%rbp)
        jmp     L_end_1
        L_else_0:
        movq    $1, %rax
        movq    %rax, -8(%rbp)
        L_end_1:
        movq    $0, %rax
        leave
        ret
        movq    $0, %rax
        leave
        ret
