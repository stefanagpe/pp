from tkinter import *
import re
expression = ""
def get_precedence(op):
    //prioritate
    if op in ('+', '-'): return 1
    if op in ('*', '/'): return 2
    return 0

def apply_op(a, b, op):
    //face operatia matematica
    if op == '+': return a + b
    if op == '-': return a - b
    if op == '*': return a * b
    if op == '/': 
        if b == 0: raise ZeroDivisionError
        return a / b

def evaluate_expression(expr):
    # tokenizarea
    tokens = re.findall(r'\d+\.?\d*|[-+*/()]', expr)
    
    values = []    # stiva pentru numere
    operators = [] # stiva pentru operatori

    i = 0
    while i < len(tokens):
        token = tokens[i]

        if token == '(':
            operators.append(token)
        elif token == ')':
            while operators and operators[-1] != '(':
                val2 = values.pop()
                val1 = values.pop()
                op = operators.pop()
                values.append(apply_op(val1, val2, op))
            operators.pop() # Scoate '('
        elif token in ('+', '-', '*', '/'):
            while (operators and operators[-1] != '(' and
                   get_precedence(operators[-1]) >= get_precedence(token)):
                val2 = values.pop()
                val1 = values.pop()
                op = operators.pop()
                values.append(apply_op(val1, val2, op))
            operators.append(token)
        else:
            # este un numar
            values.append(float(token))
        i += 1

    while operators:
        val2 = values.pop()
        val1 = values.pop()
        op = operators.pop()
        values.append(apply_op(val1, val2, op))

    return values[0]

def press(num):
    global expression
    expression = expression + str(num)
    equation.set(expression)

def equalpress():
    try:
        global expression
        # inlocuim eval(expression) cu functia noastra custom
        result = evaluate_expression(expression)
        
        # formatam rezultatul pentru a scapa de .0 daca e intreg
        if result == int(result):
            result = int(result)
            
        equation.set(str(result))
        expression = str(result)
    except Exception:
        equation.set(" error ")
        expression = ""

def clear():
    global expression
    expression = ""
    equation.set("")

if __name__ == "__main__":
    gui = Tk()
    gui.configure(background="gray")
    gui.title("Calculator Polonez")
    gui.geometry("340x200") # butoane mari

    equation = StringVar()
    expression_field = Entry(gui, textvariable=equation)
    expression_field.grid(columnspan=4, ipadx=70)

    Button(gui, text=' 1 ', fg='white', bg='blue', command=lambda: press(1), height=1, width=7).grid(row=2, column=0)
    Button(gui, text=' 2 ', fg='white', bg='blue', command=lambda: press(2), height=1, width=7).grid(row=2, column=1)
    Button(gui, text=' 3 ', fg='white', bg='blue', command=lambda: press(3), height=1, width=7).grid(row=2, column=2)
    Button(gui, text=' + ', fg='white', bg='blue', command=lambda: press("+"), height=1, width=7).grid(row=2, column=3)

    Button(gui, text=' 4 ', fg='white', bg='blue', command=lambda: press(4), height=1, width=7).grid(row=3, column=0)
    Button(gui, text=' 5 ', fg='white', bg='blue', command=lambda: press(5), height=1, width=7).grid(row=3, column=1)
    Button(gui, text=' 6 ', fg='white', bg='blue', command=lambda: press(6), height=1, width=7).grid(row=3, column=2)
    Button(gui, text=' - ', fg='white', bg='blue', command=lambda: press("-"), height=1, width=7).grid(row=3, column=3)

    Button(gui, text=' 7 ', fg='white', bg='blue', command=lambda: press(7), height=1, width=7).grid(row=4, column=0)
    Button(gui, text=' 8 ', fg='white', bg='blue', command=lambda: press(8), height=1, width=7).grid(row=4, column=1)
    Button(gui, text=' 9 ', fg='white', bg='blue', command=lambda: press(9), height=1, width=7).grid(row=4, column=2)
    Button(gui, text=' * ', fg='white', bg='blue', command=lambda: press("*"), height=1, width=7).grid(row=4, column=3)

    Button(gui, text=' 0 ', fg='white', bg='blue', command=lambda: press(0), height=1, width=7).grid(row=5, column=0)
    Button(gui, text='Clear', fg='white', bg='blue', command=clear, height=1, width=7).grid(row=5, column=1)
    Button(gui, text=' = ', fg='white', bg='blue', command=equalpress, height=1, width=7).grid(row=5, column=2)
    Button(gui, text=' / ', fg='white', bg='blue', command=lambda: press("/"), height=1, width=7).grid(row=5, column=3)

    # ptr paranteze
    Button(gui, text=' ( ', fg='white', bg='darkblue', command=lambda: press("("), height=1, width=7).grid(row=6, column=0)
    Button(gui, text=' ) ', fg='white', bg='darkblue', command=lambda: press(")"), height=1, width=7).grid(row=6, column=1)

    gui.mainloop()
