main:
        pushq   %rbp
        movq    %rsp, %rbp
        subq    $32, %rsp
        movq    $10, %rax
        movq    %rax, -8(%rbp)
        movq    $5, %rax
        movq    %rax, -16(%rbp)
        movq    -8(%rbp), %rax
        movq    %rax, %rcx
        movq    -16(%rbp), %rax
        addq    %rcx, %rax
        movq    %rax, -24(%rbp)
        movq    -8(%rbp), %rax
        movq    %rax, %rcx
        movq    -16(%rbp), %rax
        imulq   %rcx, %rax
        movq    %rax, -32(%rbp)
        movq    $0, %rax
        leave
        ret
