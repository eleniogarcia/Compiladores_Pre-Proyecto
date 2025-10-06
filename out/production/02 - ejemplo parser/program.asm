section .data
x dq 0
y dq 0

section .text
global main
main:
    mov rax, 1
    mov [x], rax
    mov rax, 2
    mov [y], rax
    mov rax, [x]
    push rax
    mov rax, 3
    push rax
    mov rax, [y]
    mov rbx, rax
    pop rax
    imul rax, rbx
    mov rbx, rax
    pop rax
    add rax, rbx
    mov [x], rax
    mov rax, [x]
    ret
