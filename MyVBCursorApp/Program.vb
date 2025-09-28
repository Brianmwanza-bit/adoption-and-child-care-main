Imports System.Windows.Forms
Module Program
    Sub Main()
        Dim form As New Form()
        form.Text = "Cursor VB App"
        Dim btn As New Button()
        btn.Text = "Click Me"
        AddHandler btn.Click, AddressOf Button_Click
        form.Controls.Add(btn)
        Application.Run(form)
    End Sub
    Sub Button_Click(sender As Object, e As EventArgs)
        MessageBox.Show("Button clicked!")
    End Sub
End Module
