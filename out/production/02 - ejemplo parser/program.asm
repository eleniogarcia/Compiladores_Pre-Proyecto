main:
        pushq   %rbp
        movq    %rsp, %rbp
        subq    $8, %rsp
        # variable z en -8(%rbp)
        movq    $2, %rax
        movq    %rax, %rbx
        movq    $3, %rax
        addq    %rbx, %rax
        movq    %rax, -8(%rbp)
        movq    $0, %rax
        leave
        ret
